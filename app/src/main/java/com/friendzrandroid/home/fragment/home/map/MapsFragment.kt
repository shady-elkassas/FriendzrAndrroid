package com.friendzrandroid.home.fragment.home.map


import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.paggingList.BaseAdapterListener
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.FragmentMapsBinding
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.MainViewModel
import com.friendzrandroid.home.adapter.OnlyEventsAroundMeAdapter
import com.friendzrandroid.home.adapter.listener.GoToEventDetailsListener
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.dialog.eventsDialog.EventsDialog
import com.friendzrandroid.home.dialog.eventsDialog.onEventSelected
import com.friendzrandroid.home.dialog.mapFilterDialog.MapFilterDialogFragment
import com.friendzrandroid.home.dialog.mapFilterDialog.MapFilterDialogListener
import com.friendzrandroid.home.dialog.tutorialDialog.TutorialDialog
import com.friendzrandroid.utils.AdsBannerUtil
import com.friendzrandroid.utils.ItemMarginEndDecoration
import com.friendzrandroid.utils.OnSwipeTouchListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.SphericalUtil
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.text.DecimalFormat


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MapsFragment : BaseFragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraChangeListener,
    GoogleMap.OnMarkerClickListener, onEventSelected,
    BaseAdapterListener<EventItemData>, OnMapReadyCallback,
    GoToEventDetailsListener<EventItemData>, MapFilterDialogListener {

    lateinit var mMap: GoogleMap
    val mapZoom = 15f
    private val animationMarkerDuration: Long = 300
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val MarkersWithData = ArrayList<Pair<MarkerOptions, Any>>()
    private val yMakker: Long = 25
    private var showNearBy: Boolean = false
    private var firstTime: Boolean = true
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var mapFragment: SupportMapFragment
    lateinit var eventAroundMeAdapterOnly: OnlyEventsAroundMeAdapter

    private var isFromEventDetails: Boolean = false
    private val selectedTags = ArrayList<TagsModel>()


    val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentMapsBinding.inflate(layoutInflater)
    }

    private val viewModel: MapViewModel by viewModels()
    private val onlyEventsAroundMeViewModel: OnlyEventsAroundMeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()


    override val baseViewModel: BaseViewModel
        get() = viewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        firstTime = true
        AdsBannerUtil.loadAds(binding.bannerAdView)


        viewModel.getUserInterests()
        userImageListener()



        mapFragment =
            (childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)


        //getMapZooming()


        val token = UserSessionManagement.getKeyAuthToken()


        if (token != null && !token.equals("")) {

            if (UserSessionManagement.getFirstOpenMapWithToken()) {
                TutorialDialog(requireContext()).showDialog("mapFilterByCategory")
                UserSessionManagement.changeFirstOpenMapWithToken()
            }
        } else {

            if (UserSessionManagement.getFirstOpenMap()) {

                UserSessionManagement.changeFirstOpenMap()

                TutorialDialog(requireContext()).showDialog("mapFilterByCategory")

            }
        }


        mapFilterSettings()
        trackScreenName("Map Screen")










        return binding.root

    }

    private fun handleDeepLinkArgument() {
        viewModel.getUserInterests()

        val bundle = this.arguments
        if (bundle != null && bundle.getString("directionId") != null) {

            var directionId = bundle.getString("directionId")


            if (directionId.equals("createEvent")) {

                addNewEvent()

            }
//            else if (directionId.equals("eventFilter")) {
//
//                binding.imgMapFilter.isChecked = true
//            }

        }

    }

    private fun handleDeepLinkEventFilterArgument() {
        viewModel.getUserInterests()

        val bundle = this.arguments
        if (bundle != null) {

            var directionId = bundle.getString("directionId")

            if (directionId.equals("eventFilter")) {

                binding.imgMapFilter.isChecked = true

//                mapFilterSettings()
            }

        }

    }


    private fun userImageListener() {
        binding.userImage.setOnClickListener {


            val token = UserSessionManagement.getKeyAuthToken()

            if (token != null && !token.equals("")) {
                findNavController().navigate(com.friendzrandroid.R.id.profileFragment)

            } else {

                startActivity(Intent(context, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }


        }
        binding.userImage.loadImage(UserSessionManagement.userImage())


    }


    private fun mapFilterSettings() {
        var showDialog: Boolean = true
        var isUpdated: Boolean = false
        val mapFilterOpen = UserSessionManagement.isMapFilterOpen()

        if (mapFilterOpen) {
            Log.d("mapFilter", "filterIs open =$mapFilterOpen")

            binding.imgMapFilter.isChecked = true
            firstTime = true

        } else {
            binding.imgMapFilter.isChecked = false

            Log.d("mapFilter", "filterIs open =$mapFilterOpen")

        }




        binding.imgMapFilter.setOnCheckedChangeListener { compoundButton, isChecked ->

//            val token = UserSessionManagement.getKeyAuthToken()
//
//            if (token != null && !token.equals("")) {


            if (!isChecked && showDialog) {
//                val tagsList = UserSessionManagement.getTagsList("UserMapFilterTags")
//                selectedTags.addAll(tagsList)

                if (selectedTags.size > 0) {

                    ConfirmationDialog(
                        requireContext(),
                        getString(com.friendzrandroid.R.string.dialog_map_filter_message), false
                    ).showDialog {

                        showDialog = it == 1
                        binding.imgMapFilter.isChecked = it != 1

                        if (it == 3) {
                            MapFilterDialogFragment.instance(
                                requireActivity().supportFragmentManager,
                                "filter by category",
                                viewModel.allInterestList,
                                selectedTags,

                                this
                            )
                            showDialog = true
                            //openMapFilter

                        } else {

                            showDialog = if (it == 1) {
                                //call map without filter and delete selected categories
                                updateMapAfterFilter()


                                true
                            } else
                                !showDialog

                        }
                    }
                } else {

                    UserSessionManagement.deleteMapFilter()
                }

            } else if (isChecked && showDialog) {

                if (UserSessionManagement.isMapFilterOpen()) {
                    val mapFilterOpen = UserSessionManagement.isMapFilterOpen()
                    Log.d("mapFilter", "filterIs open =$mapFilterOpen")

                } else if (UserSessionManagement.isMapFilterOpen() == false) {
                    val mapFilterOpen = UserSessionManagement.isMapFilterOpen()

                    Log.d("mapFilter", "filterIs open =$mapFilterOpen")

                    MapFilterDialogFragment.instance(
                        requireActivity().supportFragmentManager,
                        "Events’ filter",
                        viewModel.allInterestList,
                        selectedTags,

                        this
                    )

                }
            }

        }
        //           else {
//
//                startActivity(Intent(context, AuthActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                })
//            }
//
//        }


//        AllowMyApperanceDialog(requireContext()).showDialog {
//
////                        Log.e("Feed", "onCreateView: $it")
////                        Log.e("Feed", "onCreateView: ${requireContext().packageName}")
//            //isUpdated = true
//
//            if (it.size == 0) {
//                showDialog = false
//                binding.imgMapFilter.isChecked = false
//                showDialog = true
//            } else {
//                if (!isUpdated) {
//                    viewModel.updateUserPrivateMode(it, true)
//                    isUpdated = true
//                }
//            }
//        }


        //        binding.imgPrivateModes.setOnCheckedChangeListener { compoundButton, isChecked ->
//            if (!isChecked)
//                ConfirmationDialog(
//                    requireContext(),
//                    getString(R.string.dialog_private_mode_message)
//                ).showDialog {
//                    binding.imgPrivateModes.isChecked.apply { it != 1 }
//                        .also {
//                            viewModel.updateUserPrivateMode(
//                                UserSessionManagement.getMyAppearanceType().toList(),
//                                false
//                            )
//                        }
//                }
//            else
//                AllowMyApperanceDialog(requireContext()).showDialog {
//                    if (it.size == 0)
//                        binding.imgPrivateModes.isChecked = false
//                    else
//                        viewModel.updateUserPrivateMode(it, true)
//                }
//        }

    }

    private fun updateMapAfterFilter() {

//        showNearBy = false
//        binding.rcMapBottomSheetEventAroundMeList.hide()
//        binding.arrow.rotation = 0F
//        binding.noDataFoundTxt.hide()

//todo updateMapAfterFilter

        selectedTags.clear()
        UserSessionManagement.deleteMapFilter()
        initObs("")



        initRecyclerView("")

    }

    private fun getUserLocation() {


        var gpsTracker = GpsTracker(context)
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.latitude
            longitude = gpsTracker.longitude


            Log.d("map", "getUserLocation: lat is $latitude ")
            Log.d("map", "getUserLocation: long is $longitude ")
        } else {
            gpsTracker.showSettingsAlert()
        }
    }


    fun initObs(filterSelectedTags: String) {

//        viewModel.getAllEvents()

        getUserLocation()

        viewModel.userLat = latitude
        viewModel.userLong = longitude
        Log.d("map", "getUserLocation: lat is $latitude ")
        Log.d("map", "getUserLocation: long is $longitude ")
        viewModel.getAroundMeData(filterSelectedTags)

        if (!filterSelectedTags.equals("")) {
        }

        viewModel._eventsGD.observe(viewLifecycleOwner, {
            setGenderDistEvent(it)

        })


        viewModel._allLocationEvents.observe(viewLifecycleOwner, {

            setAllLocationEvents(it)


        })


//        viewModel.eventsByLocation.observe(viewLifecycleOwner, {
//            setEventsByLocation(it)
//        })


        mainViewModel.isLocationEnabled.observe(viewLifecycleOwner, {
            if (it) {

                if (mMap != null) {

                    mMap.isMyLocationEnabled = true

                }

            }
        })


    }


    override fun onMyLocationButtonClick(): Boolean {

        (requireActivity() as MainActivity).locationUtils.locationStatus()
        return false
    }


    fun moveCamera(latitude: Double, longitude: Double, zoom: Float) {
        val googlePlex = CameraPosition.builder()
            .target(LatLng(latitude, longitude))
            .zoom(zoom)
            .bearing(0f)
            .tilt(0f)
            .build()

        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), null)



        getMapZooming()

    }

    private fun setGenderDistEvent(data: List<LocationGenderDistribution>) {


        val marker = MarkerOptions()

        val bitmapDescriptorFromVector1 =
            bitmapDescriptorFromVector(requireContext(), R.drawable.ic_marker_green)

        data.forEach {

            marker.position(LatLng(it.lat, it.lang))

            marker.icon(
                bitmapDescriptorFromVector1
            )


//            MarkersWithData.add(Pair(marker, it))
            mMap.addMarker(marker)
        }


    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    //todo map locations
    private fun setAllLocationEvents(data: List<EventMapResponseItem>) {


        val privateEventMarker: View =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                com.friendzrandroid.R.layout.custom_proximity_pin_private,
                null
            )

        val privateEventMarkerText =
            privateEventMarker.findViewById<View>(com.friendzrandroid.R.id.tv_marker_text) as TextView

        val externalEventMarker: View =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                com.friendzrandroid.R.layout.custom_proximity_pin_gray,
                null
            )

        val externalEventMarkerText =
            externalEventMarker.findViewById<View>(com.friendzrandroid.R.id.tv_marker_text) as TextView


        val whiteLabelEventMarker: View =

            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                com.friendzrandroid.R.layout.custom_proximity_pin_white_label,
                null
            )

        var imageView =
            whiteLabelEventMarker.findViewById(com.friendzrandroid.R.id.tv_marker_text) as ImageView


        val markernew: View =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                com.friendzrandroid.R.layout.custome_proximity_pin_green,
                null
            )

        val tv_marker_text =
            markernew.findViewById<View>(com.friendzrandroid.R.id.tv_marker_text) as TextView


//        val marker = MarkerOptions()

        data.forEach {

            val marker = MarkerOptions()
            marker.position(LatLng(it.lat, it.lang))


            if (it.eventTypeName == "Private") {


                privateEventMarkerText.text = it.eventData.size.toString()
                marker.icon(createDrawableFromView(privateEventMarker)?.let { it1 ->
                    BitmapDescriptorFactory.fromBitmap(
                        it1
                    )
                })

                MarkersWithData.add(Pair(marker, it))
                mMap.addMarker(marker)


            } else if (it.eventTypeName == "Friendzr") {

                tv_marker_text.text = it.eventData.size.toString()


                marker.icon(createDrawableFromView(markernew)?.let { it1 ->
                    BitmapDescriptorFactory.fromBitmap(
                        it1
                    )
                })



                MarkersWithData.add(Pair(marker, it))

                mMap.addMarker(marker)
            } else if (it.eventTypeName == "Whitelabel") {


                val image = it.eventMarkerImage

                Log.d("eventDate.UserImage", "eventDate.UserImage is: $image")
                Picasso.get().load(image).into(imageView)

                marker.icon(createDrawableFromView(whiteLabelEventMarker)?.let { it1 ->
                    BitmapDescriptorFactory.fromBitmap(
                        it1
                    )
                })


                MarkersWithData.add(Pair(marker, it))


                mMap.addMarker(marker)


            } else {
                externalEventMarkerText.text = it.eventData.size.toString()

                marker.icon(createDrawableFromView(externalEventMarker)?.let { it1 ->
                    BitmapDescriptorFactory.fromBitmap(
                        it1
                    )
                })

                MarkersWithData.add(Pair(marker, it))
                mMap.addMarker(marker)


            }
            it.eventData.forEach { eventDate ->


            }

        }


        handleDeepLinkEventFilterArgument()


    }

    private fun setAllLocationEvents2(data: List<EventMapResponseItem>) {

        data.forEach {
            val marker = MarkerOptions()

            marker.position(LatLng(it.lat, it.lang))

            val markernew: View =
                (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    com.friendzrandroid.R.layout.custome_proximity_pin_green,
                    null
                )

            val tv_marker_text =
                markernew.findViewById<View>(com.friendzrandroid.R.id.tv_marker_text) as TextView
            tv_marker_text.text = it.eventData.size.toString()


            val privateEventMarker: View =
                (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    com.friendzrandroid.R.layout.custom_proximity_pin_private,
                    null
                )

            val whiteLabelEventMarker: View =

                (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    com.friendzrandroid.R.layout.custom_proximity_pin_white_label,
                    null
                )

            val privateEventMarkerText =
                privateEventMarker.findViewById<View>(com.friendzrandroid.R.id.tv_marker_text) as TextView
            privateEventMarkerText.text = it.eventData.size.toString()


            val externalEventMarker: View =
                (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    com.friendzrandroid.R.layout.custom_proximity_pin_gray,
                    null
                )

            val externalEventMarkerText =
                externalEventMarker.findViewById<View>(com.friendzrandroid.R.id.tv_marker_text) as TextView
            externalEventMarkerText.text = it.eventData.size.toString()

            it.eventData.forEach { eventDate ->
                if (eventDate.eventTypeName == "Private") {
                    marker.icon(createDrawableFromView(privateEventMarker)?.let { it1 ->
                        BitmapDescriptorFactory.fromBitmap(
                            it1
                        )
                    })
                    MarkersWithData.add(Pair(marker, it))
                    mMap.addMarker(marker)
                } else if (eventDate.eventTypeName == "Friendzr") {

                    marker.icon(createDrawableFromView(markernew)?.let { it1 ->
                        BitmapDescriptorFactory.fromBitmap(
                            it1
                        )
                    })



                    MarkersWithData.add(Pair(marker, it))

                    mMap.addMarker(marker)
                } else if (eventDate.eventTypeName == "Whitelabel") {

                    val whiteLabelEventMarker: View =

                        (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                            com.friendzrandroid.R.layout.custom_proximity_pin_white_label,
                            null
                        )

                    var imageView =
                        whiteLabelEventMarker.findViewById(com.friendzrandroid.R.id.tv_marker_text) as ImageView

                    val image = it.eventMarkerImage

                    Log.d("eventDate.UserImage", "eventDate.UserImage is: $image")
                    Picasso.get().load(image).into(imageView)

                    marker.icon(createDrawableFromView(whiteLabelEventMarker)?.let { it1 ->
                        BitmapDescriptorFactory.fromBitmap(
                            it1
                        )
                    })


                    MarkersWithData.add(Pair(marker, it))


                    mMap.addMarker(marker)


                } else {
                    marker.icon(createDrawableFromView(externalEventMarker)?.let { it1 ->
                        BitmapDescriptorFactory.fromBitmap(
                            it1
                        )
                    })

                    MarkersWithData.add(Pair(marker, it))
                    mMap.addMarker(marker)


                }

            }

        }


        handleDeepLinkEventFilterArgument()


    }

    private fun setUpMarkerImage(mMap: GoogleMap, image: String) {


    }


    private fun setEventsByLocation(data: List<EventMapData>) {


        data.forEach {
            val marker = MarkerOptions()
            marker.icon(
                requireActivity().bitmapDescriptorFromVector(
                    com.friendzrandroid.R.drawable.ic_marker_green,
                    null, data.size.toString()
                )
            )
            marker.position(LatLng(it.lat.toDouble(), it.lang.toDouble()))

            MarkersWithData.add(Pair(marker, it))
            mMap.addMarker(marker)
        }

    }


    override fun onMarkerClick(p0: Marker): Boolean {
        val eventLocation = MarkersWithData.find { it.first.position == p0.position }

        eventLocation?.let {

            when (eventLocation.second) {
                is EventMapResponseItem -> {

                    val data = eventLocation.second as EventMapResponseItem

                    if (data.eventData.size == 1) {
                        findNavController().navigate(
                            MapsFragmentDirections.actionNavigationToEventDetails(data.eventData[0].id)
                        )
                    } else {

                        EventsDialog.instance(
                            requireActivity().supportFragmentManager,
                            data.eventData,
                            this
                        )
                    }


                }

                is EventMapData -> {

                    try {
                        val x = (it.second as EventMapData)
                        findNavController().navigate(
                            MapsFragmentDirections.actionNavigationToEventDetails(x.id)
                        )
                    } catch (e: Exception) {
                    }
                }

                is LocationGenderDistribution -> {

                    //this action has disapled for now
//                    val data = (it.second as LocationGenderDistribution)
//                    findNavController().navigate(
//                        MapsFragmentDirections.actionNavigationToGenderDistribution(data)
//                    )


                }

            }


        }
        return true
    }

    fun onCameraChangeLisener() {

        var camaraStartMove = true
        val handler = Handler(Looper.getMainLooper())
        var runnable = {
            if (!camaraStartMove) {
                latitude = mMap.cameraPosition.target.latitude
                longitude = mMap.cameraPosition.target.longitude

                Log.e("TAG", "onCameraChangeLisener: $latitude, $longitude")

//                viewModel.getEventsByLocation(mMap.cameraPosition.target)


            }


        }
        mMap.setOnCameraIdleListener {
            Log.d("camara ", "Idel Camera")
            binding.marker.animate().setDuration(animationMarkerDuration).setListener(object :
                Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    if (binding.markerPosition.y == binding.marker.y) {
                        Log.d("camara ", "Idel Camera animation")

                        binding.ivMarkerPoint.alpha = 0f

                        camaraStartMove = false
                        handler.postDelayed(
                            runnable,
                            animationMarkerDuration
                        )
                    }

                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }

            }).y(binding.markerPosition.y).interpolator =
                AccelerateDecelerateInterpolator()
        }


        mMap.setOnCameraMoveStartedListener {
            camaraStartMove = true
            handler.removeCallbacksAndMessages(null)
            if (binding.markerPosition.y > 0) {
                binding.marker.animate().setDuration(animationMarkerDuration)
                    .y(binding.markerPosition.y - yMakker).interpolator = DecelerateInterpolator()


                binding.ivMarkerPoint.animate().alpha(1f).duration = animationMarkerDuration / 2
            }


            getMapZooming()

        }
        getMapZooming()


    }


    override fun EventData(dialog: EventsDialog, data: EventMapData) {

        dialog.dismiss()


        findNavController().navigate(
            MapsFragmentDirections.actionNavigationToEventDetails(data.id)
        )


    }

    override fun onItemSelected(data: EventItemData) {

        moveCamera(data.lat?.toDouble()!!, data.lang?.toDouble()!!, mapZoom)


    }

    override fun goToEventDetails(data: EventItemData) {
        isFromEventDetails = true
        findNavController().navigate(
            MapsFragmentDirections.actionNavigationToEventDetails(data.id)
        )
    }


    override fun onPause() {
        super.onPause()
        binding.marker.hide()
        binding.ivMarkerPoint.alpha = 0f

        if (!isFromEventDetails) {

            viewModel.deleteLiveData()
            mMap.clear()
        }


    }

    override fun onStop() {
        super.onStop()
//


    }

    override fun onResume() {
        super.onResume()
        var convertTagsToUpdate = ""
        if (selectedTags.size > 0) {
            convertTagsToUpdate = convertTagsToUpdate(selectedTags)

        }
        if (!isFromEventDetails) {


            mapFragment.onResume()

            if (!this::eventAroundMeAdapterOnly.isInitialized) {

                initRecyclerView(convertTagsToUpdate)
            } else {
                eventAroundMeAdapterOnly.reloadData()
            }
        }


    }


    private fun initRecyclerView(filterSelectedTags: String) {
        getUserLocation()

        onlyEventsAroundMeViewModel.userLat = latitude
        onlyEventsAroundMeViewModel.userLang = longitude
        onlyEventsAroundMeViewModel.filterSelectedTags = filterSelectedTags

        eventAroundMeAdapterOnly =
            OnlyEventsAroundMeAdapter(onlyEventsAroundMeViewModel, this, this)

        binding.rcMapBottomSheetEventAroundMeList.apply {
            adapter = eventAroundMeAdapterOnly
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            //         LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }


        val endItemDecoration = ItemMarginEndDecoration()
        binding.rcMapBottomSheetEventAroundMeList.addItemDecoration(endItemDecoration)

        eventAroundMeAdapterOnly.reloadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment.onDestroy()


//        viewModel._allLocationEvents.value = null
//        viewModel._eventsGD.value = null


    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment.onLowMemory()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val mapFilterOpen = UserSessionManagement.isMapFilterOpen()

        if (mapFilterOpen) {
            val tagsList = UserSessionManagement.getTagsList("UserMapFilterTags")
            selectedTags.addAll(tagsList)
        }


        if (!isFromEventDetails) {
            callMapConfiguration(googleMap)

        }

        handleDeepLinkArgument()


    }

    private fun callMapConfiguration(googleMap: GoogleMap) {
        var convertTagsToUpdate = ""
        getUserLocation()
        googleMap.setOnMarkerClickListener(this)
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        setMapSettings()
        getMapZooming()
        getCurrentZoom()
        if (selectedTags.size > 0) {
            convertTagsToUpdate = convertTagsToUpdate(selectedTags)

        }
        initObs(convertTagsToUpdate)

    }


    private fun setMapSettings() {


        binding.currentLocation.setOnClickListener {
            getUserLocation()
            moveCamera(latitude, longitude, mapZoom)
            Log.d("map", "getUserLocation: lat is $latitude ")
            Log.d("map", "getUserLocation: long is $longitude ")
        }

        binding.changeMap.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {

                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


            } else {

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        }


        binding.addNewEvent.setOnClickListener {

            addNewEvent()


        }
        viewNearByEventsVisibilitySettings()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latitude, longitude),
                mapZoom
            )

        )

    }

    private fun addNewEvent() {
        val token = UserSessionManagement.getKeyAuthToken()

        if (token != null && !token.equals("")) {

            if (binding.marker.isVisible) {
                val action =
                    MapsFragmentDirections.actionNavigationToAddEvent(
                        locationLat = mMap.cameraPosition.target.latitude.toString(),
                        locationLang = mMap.cameraPosition.target.longitude.toString()
                    )


                findNavController().navigate(action)
            } else {
                mainViewModel.isLocationEnabled.value?.let {
                    if (it) {
                        binding.marker.show()
                        onCameraChangeLisener()
                    } else {
                        (requireActivity() as MainActivity).locationUtils.locationStatus()
                    }
                }
            }

        } else {

            startActivity(Intent(context, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun viewNearByEventsVisibilitySettings() {

        binding.llExploarMoreHeader.setOnTouchListener(object :
            OnSwipeTouchListener(context) {
            override fun onSwipeTop() {
                nearByVisbility()
            }

            override fun onSwipeBottom() {
                nearByVisbility()
            }


            override fun onClick() {
                nearByVisbility()
            }
        }

        )

    }


    private fun nearByVisbility() {
        if (!showNearBy) {
            showNearBy = true

            //todo nearByVisbility
            binding.rcMapBottomSheetEventAroundMeList.show()

            binding.arrow.rotation = 180F

            onlyEventsAroundMeViewModel.totalItemCount.observe(viewLifecycleOwner) {
                if (it > 0) {
                    binding.rcMapBottomSheetEventAroundMeList.show()

                    binding.noDataFoundTxt.hide()
                } else {

                    binding.rcMapBottomSheetEventAroundMeList.hide()
                    binding.noDataFoundTxt.show()
                }
            }
        } else {
            //todo hide nearby events

            showNearBy = false
            binding.rcMapBottomSheetEventAroundMeList.hide()
            binding.arrow.rotation = 0F
            binding.noDataFoundTxt.hide()


        }
    }

    private fun getCurrentZoom() {
        val visibleRegion = mMap.projection.visibleRegion

        val distance: Double = SphericalUtil.computeDistanceBetween(
            visibleRegion.farLeft, LatLng(latitude, longitude)

        )
        Log.d("map", "getUserLocation: lat is $latitude ")
        Log.d("map", "getUserLocation: long is $longitude ")

        val format = DecimalFormat("0.#")
        val format1 = format.format(distance / 1000 * 1000)
        val format2 = format.format(distance / 1000)

        val zoomDistance = "$format1 m | $format2 km"
        binding.tvZoomText.setText(zoomDistance)
    }

    private fun getMapZooming() {

        mMap.setOnCameraMoveStartedListener {
            val visibleRegion = mMap.projection.visibleRegion

            val distance: Double = SphericalUtil.computeDistanceBetween(
                visibleRegion.farLeft, LatLng(latitude, longitude)

            )
            Log.d("map", "getUserLocation: lat is $latitude ")
            Log.d("map", "getUserLocation: long is $longitude ")

            val format = DecimalFormat("0.#")
            val format1 = format.format(distance / 1000 * 1000)
            val format2 = format.format(distance / 1000)

            val zoomDistance = "$format1 m | $format2 km"
            binding.tvZoomText.setText(zoomDistance)
        }


    }

    override fun onCameraMoveStarted(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onCameraChange(p0: CameraPosition) {
        TODO("Not yet implemented")
    }

    private fun createDrawableFromView(view: View): Bitmap? {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onMapFilterSave(selectedTags: ArrayList<TagsModel>) {


        val token = UserSessionManagement.getKeyAuthToken()

        if (token != null && !token.equals("")) {
            val size = selectedTags.size
            if (selectedTags.size > 0) {

                UserSessionManagement.saveMapFilter(selectedTags)


                this.selectedTags.clear()
                this.selectedTags.addAll(selectedTags)


                val filterSelectedTags = convertTagsToUpdate(selectedTags)


                viewModel.deleteLiveData()
                mMap.clear()

                initObs(filterSelectedTags)
                initRecyclerView(filterSelectedTags)

                Log.d("mapFilter", "selectedTags.size=$size")

            } else {
                Log.d("mapFilter", "selectedTags.size=$size")

//            binding.imgMapFilter.setChecked(false)

                binding.imgMapFilter.isChecked = false
                UserSessionManagement.deleteMapFilter()


            }
        } else {

            startActivity(Intent(context, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

    }

    override fun onMapFilterDismiss(isDismiss: Boolean) {

        if (isDismiss) {
            if (UserSessionManagement.isMapFilterOpen() == false) {

                Log.d("mapFilter", "UserSessionManagement.isMapFilterOpen() == false")
                binding.imgMapFilter.isChecked = false
            } else {
                Log.d("mapFilter", "UserSessionManagement.isMapFilterOpen() == true")
            }


        }
    }

    private fun convertTagsToUpdate(data: List<TagsModel>): String {
        var strIDS = "["
        data.forEach {
            strIDS += "\"${it.tagID}\","
        }

        strIDS = strIDS.substring(0, strIDS.length - 1)
        strIDS += "]"

        return strIDS
    }
}