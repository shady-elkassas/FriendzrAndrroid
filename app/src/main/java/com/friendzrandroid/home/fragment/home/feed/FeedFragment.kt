package com.friendzrandroid.home.fragment.home.feed

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.bumptech.glide.Glide
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.FeedFragmentBinding
import com.friendzrandroid.home.adapter.FeedsAdapter
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.dialog.gostModeDialog.AllowMyApperanceDialog
import com.friendzrandroid.home.dialog.tutorialDialog.TutorialDialog
import com.friendzrandroid.utils.AdsBannerUtil
import com.friendzrandroid.utils.ItemMarginBottomDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics.Event.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.abs


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FeedFragment : BaseFragment(), FeedRequestAdapterListener<FeedRequestUserData>,
    SensorEventListener {

    companion object {
        var isActive: Boolean = false

    }

//    lateinit var navigationView: BottomNavigationView

    private var showDialog: Boolean = true

    private lateinit var receiver: BroadcastReceiver

    private lateinit var recAdapter: FeedsAdapter

    val LOG_TAG = "AppsFlyerOneLinkSimApp"
    var conversionData: Map<String, Any>? = null

    private var currentDegree = 0f
    private lateinit var sensorManager: SensorManager


    var deepLinkFirstTime: Boolean = true
    var deepLinkValue: String? = ""
    var referrerId: String? = ""
    var deepLinkSub1: String? = ""
    var deepLinkSub2: String? = ""

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FeedFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel: FeedViewModel by viewModels()
    override val baseViewModel: BaseViewModel
        get() = viewModel

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        broadCastReceiver()

        if (UserSessionManagement.isWhiteLabel()) {
            findNavController().navigate(FeedFragmentDirections.actionNavigationFeedToNavigationInbox())
        }


//        requireActivity().getOnBackPressedDispatcher()
//            .addCallback(requireActivity(), onBackPressedCallback)


        binding.swipeToRefresh.changeColor()

        getUserLocation()

        AdsBannerUtil.loadAds(binding.bannerAdView)

//        createBannerAd()
//
//        AppLovinSdk.getInstance(requireContext()).showMediationDebugger()


        sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager


        checkFeedListCountToMakeOperation()

        viewModel.messageError.observe(viewLifecycleOwner) {
            binding.noDataFoundTxt.text = it
        }



        binding.swipeToRefresh.setOnRefreshListener {
            getUserLocation()
            viewModel.reload()
        }
        viewModel.getSettings()



        privateModeSettings()

        userImageListener()


        val token = UserSessionManagement.getKeyAuthToken()


        if (token != null && !token.equals("")) {

            if (UserSessionManagement.getFirstOpenFeedWithToken()) {

//            handleInstructionView()
                UserSessionManagement.changeFirstOpenFeedWithToken()

                if (!UserSessionManagement.isWhiteLabel()) {
                    TutorialDialog(requireContext()).showDialog("compass")
                }
            }

        } else {

            if (UserSessionManagement.getFirstOpenFeed()) {

//            handleInstructionView()
                UserSessionManagement.changeFirstOpenFeed()

                if (!UserSessionManagement.isWhiteLabel()) {
                    TutorialDialog(requireContext()).showDialog("compass")
                }

            }
        }


        compassListener()
        interestsMatchListener()



        trackScreenName("Feed Screen")



        initAppsFlyer()


//        val bundle = this.arguments
//        if (bundle != null) {
//            if (bundle.getString("directionId") != null) {
//                val directionId = bundle.getString("directionId")
//
//                if (directionId.equals("directionalFiltering")) {
//                    binding.compassButton.isChecked = true
//                }
//            }
//
//        } else {
//
//
//        }


//
//        val directionName = requireActivity().intent.getStringExtra("directionName")


        viewModel.reLoadFeed.observe(viewLifecycleOwner) {


//            if (!this::recAdapter.isInitialized) {
//
//                initRecyclerView()
//            } else {
//                recAdapter.reloadData()
//            }

//            onResume()

            val fragmentId = findNavController().currentDestination?.id
            findNavController().popBackStack(fragmentId!!,true)
            findNavController().navigate(fragmentId)

        }

        return binding.root

    }


    private fun initAppsFlyer() {

//        val bottomNavigationView =
//            requireActivity().findViewById<BottomNavigationView>(com.friendzrandroid.R.id.nav_view)
//        bottomNavigationView.visibility = View.GONE


        val appsflyer = AppsFlyerLib.getInstance()
        appsflyer.setDebugLog(true)

//        appsflyer.setMinTimeBetweenSessions(0)
//        AppsFlyerLib.getInstance().setAppInviteOneLink("59hw")


        appsflyer.subscribeForDeepLink(DeepLinkListener { deepLinkResult ->
            val dlStatus = deepLinkResult.status
            if (dlStatus == DeepLinkResult.Status.FOUND) {
                Log.d(LOG_TAG, "Deep link found")
            } else if (dlStatus == DeepLinkResult.Status.NOT_FOUND) {
                Log.d(LOG_TAG, "Deep link not found")
                return@DeepLinkListener
            } else {
                // dlStatus == DeepLinkResult.Status.ERROR
                val dlError = deepLinkResult.error
                Log.d(LOG_TAG, "There was an error getting Deep Link data: $dlError")
                return@DeepLinkListener
            }


            val deepLinkObj = deepLinkResult.deepLink
            try {
                Log.d(LOG_TAG, "The DeepLink data is: $deepLinkObj")
            } catch (e: Exception) {
                Log.d(LOG_TAG, "DeepLink data came back null")
                return@DeepLinkListener
            }
            // An example for using is_deferred
            if (deepLinkObj.isDeferred!!) {
                Log.d(LOG_TAG, "This is a deferred deep link")
            } else {
                Log.d(LOG_TAG, "This is a direct deep link")
            }
            // An example for getting deep_link_value
//            var deepLinkValue: String? = ""
//            var referrerId: String? = ""
//            var deepLinkSub1: String? = ""
//            var deepLinkSub2: String? = ""

            deepLinkValue = deepLinkObj.deepLinkValue
            val dlData = deepLinkObj.clickEvent

            try {


                if (deepLinkValue == null || deepLinkValue == "") {
                    Log.d(LOG_TAG, "deep_link_value returned null")
                    deepLinkValue = deepLinkObj.getStringValue("checkOut")
                    Log.d(LOG_TAG, "deepLinkValue is :$deepLinkValue")


                    if (deepLinkValue == null || deepLinkValue == "") {
                        Log.d(LOG_TAG, "could not find fruit name")
                        return@DeepLinkListener
                    }
                    Log.d(LOG_TAG, "fruit_name is $deepLinkValue. This is an old link")
                } else {


//                    if (navigationView.visibility == View.GONE) {
//                        navigationView.visibility = View.VISIBLE
//                    } else {
//                        var x = 0
//                    }


                    deepLinkSub1 = deepLinkObj!!.getStringValue("deep_link_sub1")

                    if (deepLinkSub1 != null) {
                        if (UserSessionManagement.getKeyAuthToken() != null) {
//                            goToFragment(deepLinkSub1, "feed")


                            if (deepLinkValue.equals("checkOut")) {

//                                handleCheckOutLink(dlData, deepLinkValue, deepLinkObj)


                            } else if (deepLinkValue.equals("event")) {
                                goToFragment(
                                    "eventID",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.eventDetailsFragment
                                )

                            } else if (deepLinkValue.equals("createEvent")) {

                                goToFragment(
                                    "directionId",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.navigation_map
                                )
                            } else if (deepLinkValue.equals("eventFilter")) {
                                goToFragment(
                                    "directionId",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.navigation_map
                                )

                            }  else if (deepLinkValue.equals("map")) {
                                goToFragment(
                                    "directionId",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.navigation_map
                                )

                            } else if (deepLinkValue.equals("feed")) {

//                                goToFragment(deepLinkSub1, "feed")


                            } else if (deepLinkValue.equals("directionalFiltering")) {

//                                binding.compassButton.isChecked = true
//                                binding.compassContainer.show()
//                                registerSensorForCompass(true)


//                                goToFragment("directionId", deepLinkSub1!!, R.id.navigation_feed)


                            } else if (deepLinkValue.equals("login")) {

                                if (UserSessionManagement.getKeyAuthToken() != null) {
//                                    goToFragment(deepLinkSub1, "feed")

                                } else {
                                    val intent = Intent(requireActivity(), AuthActivity::class.java)
                                    intent.putExtra("directionName", "login");
                                    intent.putExtra("directionId", "login");

                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }


                            } else if (deepLinkValue.equals("interests")) {

                                goToFragment(
                                    "directionId",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.editProfileFragment
                                )


                            } else if (deepLinkValue.equals("editProfile")) {
//                                bottomNavigationView.visibility = View.GONE
                                goToFragment(
                                    "directionId",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.editProfileFragment
                                )
                            } else if (deepLinkValue.equals("personalSpace")) {
                                goToFragment(
                                    "personalSpace",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.settingsFragment
                                )
                            } else if (deepLinkValue.equals("ageFilter")) {
                                goToFragment(
                                    "ageFilter",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.settingsFragment
                                )


                            } else if (deepLinkValue.equals("privateMode")) {
                                goToFragment(
                                    "privateMode",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.settingsFragment
                                )


                            } else if (deepLinkValue.equals("distanceFilter")) {
                                goToFragment(
                                    "distanceFilter",
                                    deepLinkSub1!!,
                                    com.friendzrandroid.R.id.settingsFragment
                                )
                            }

//                            else if (deepLinkValue.equals("eventFilter")) {
//                                goToFragment(
//                                    "directionId",
//                                    deepLinkSub1!!,
//                                    com.friendzrandroid.R.id.navigation_map
//                                )
//                            }


                        } else {
                            val intent = Intent(requireActivity(), AuthActivity::class.java)
                            intent.putExtra("directionName", "login");
                            intent.putExtra("directionId", "login");

                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)

//                            if (deepLinkValue.equals("login")) {
//
//                                if (UserSessionManagement.getKeyAuthToken() != null) {
//                                    goToFragment(deepLinkSub1, "feed")
//
//                                } else {
//
//                                }
//
//
//                            }
                        }


                    }
                }

                if (dlData.has("deep_link_sub1")) {

                    deepLinkSub1 = deepLinkObj.getStringValue("deep_link_sub1")
                    Log.d(LOG_TAG, "The referrerID is: $referrerId")

                }
                if (dlData.has("deep_link_sub2")) {

                    deepLinkSub2 = deepLinkObj.getStringValue("deep_link_sub2")
                    Log.d(LOG_TAG, "The referrerID is: $referrerId")
                } else {
                    Log.d(LOG_TAG, "deep_link_sub2/Referrer ID not found")
                }





                Log.d(LOG_TAG, "The DeepLink will route to: $deepLinkValue")
            } catch (e: Exception) {
                Log.d(LOG_TAG, "There's been an error: $e")
                return@DeepLinkListener
            }
        })

        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionDataMap: MutableMap<String, Any>) {
                for (attrName in conversionDataMap.keys) Log.d(
                    LOG_TAG,
                    "Conversion attribute keys: " + attrName + " = " + conversionDataMap[attrName]
                )
                val status = Objects.requireNonNull(conversionDataMap["af_status"]).toString()
                if (status == "Non-organic") {
                    if (Objects.requireNonNull(conversionDataMap["is_first_launch"])
                            .toString() == "true"
                    ) {

                        //Deferred deep link in case of a legacy link
                        if (conversionDataMap.containsKey("fruit_name")) {
                            if (conversionDataMap.containsKey("deep_link_value")) { //Not legacy link
                                Log.d(
                                    LOG_TAG,
                                    "onConversionDataSuccess: Link contains deep_link_value, deep linking with UDL"
                                )
                            } else {

                                //Legacy link
                                conversionDataMap["deep_link_value"] = conversionDataMap["event"]!!

                                val deepLinkValueStr = conversionDataMap["event"] as String?

                                val deepLinkData: DeepLink =
                                    mapToDeepLinkObject(conversionDataMap)!!
                                val deepLinkValue = deepLinkData.deepLinkValue
                                val deepLinkSub1 = deepLinkData.afSub1


                            }


                        }


                    } else {
                        Log.d(LOG_TAG, "Conversion: Not First Launch")
                    }
                } else {
                    Log.d(LOG_TAG, "Conversion: This is an organic install.")
                }
                conversionData = conversionDataMap
            }

            override fun onConversionDataFail(errorMessage: String) {
                Log.d(LOG_TAG, "error getting conversion data: $errorMessage")
            }

            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                Log.d(LOG_TAG, "onAppOpenAttribution: This is fake call.")
            }

            override fun onAttributionFailure(errorMessage: String) {
                Log.d(LOG_TAG, "error onAttributionFailure : $errorMessage")
            }
        }

        appsflyer.init("vsg4WBcUHeJBLTpcHDpuJ", conversionListener, requireActivity())
        appsflyer.start(requireActivity())
        appsflyer.setDebugLog(true)

    }

    fun mapToDeepLinkObject(conversionDataMap: MutableMap<String, Any>): DeepLink? {
        try {
            val objToStr = Gson().toJson(conversionDataMap)
            val deepLink: DeepLink = DeepLink.AFInAppEventParameterName(JSONObject(objToStr))



            return deepLink
        } catch (e: JSONException) {
            Log.d(
                LOG_TAG, "Error when converting map to DeepLink object: $e"
            )
        }

        return null
    }


//    private fun goToFragment(directionId: String, directionName: Int) {
//
////        val intent = Intent(requireContext(), MainActivity::class.java)
////        intent.putExtra("directionName", directionName);
////        intent.putExtra("directionId", directionId);
////
////        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
////        startActivity(intent)
//
//
//        binding.userImage.setOnClickListener {
//            findNavController().navigate(com.friendzrandroid.R.id.profileFragment)
//
//        }
//    }

    private fun goToFragment(
        argumentIdKey: String, argumentIdValue: String, fragment: Int
    ) {
//        val navController = findNavController(
//            R.id.nav_host_fragment_activity_main
//        )
//                    navController.navigateUp()

        val bundle = Bundle()
        bundle.putString(argumentIdKey, argumentIdValue)
        findNavController().navigate(fragment, bundle)
    }

    private fun userImageListener() {

        binding.userImage.setOnClickListener {
            findNavController().navigate(com.friendzrandroid.R.id.profileFragment)

        }

    }

    private fun checkFeedListCountToMakeOperation() {

        viewModel.totalItemCount.observe(viewLifecycleOwner) {
            binding.swipeToRefresh.isRefreshing = false

            if (it > 0) {
                binding.FeedRecycler.show()
                binding.noDataContainer.hide()
                if (deepLinkValue.equals("directionalFiltering")) {

                    if (deepLinkFirstTime) {
                        deepLinkFirstTime = false
                        binding.compassButton.isChecked = true
                        binding.compassContainer.visibility = View.VISIBLE
                        registerSensorForCompass(true)
                    }

//                                goToFragment("directionId", deepLinkSub1!!, R.id.navigation_feed)


                }
//                binding.noDataFoundTxt.hide()
//                binding.imgNoData.hide()
            } else {
                binding.FeedRecycler.hide()
                binding.noDataContainer.show()

                if (binding.imgPrivateModes.isChecked) {
                    Glide.with(this).load(com.friendzrandroid.R.drawable.ic_no_data_private_mode)
                        .into(binding.imgNoData)

                    binding.noDataFoundTxt.text =
                        resources.getString(com.friendzrandroid.R.string.no_data_feed_private_mode)
                } else {
                    Glide.with(this).load(com.friendzrandroid.R.drawable.ic_no_data)
                        .into(binding.imgNoData)

                    binding.noDataFoundTxt.text =
                        resources.getString(com.friendzrandroid.R.string.no_data_feed)
                }

//                binding.noDataFoundTxt.show()
//                binding.noDataFoundTxt.text = resources.getString(R.string.noData)
//                binding.imgNoData.show()
            }
        }
    }


    private fun interestsMatchListener() {
        binding.imgInterestMatch.setOnCheckedChangeListener { compoundButton, isChecked ->

            val token = UserSessionManagement.getKeyAuthToken()


            if (token != null && !token.equals("")) {
                if (!isChecked) {
//                binding.imgInterestMatch.isChecked = false

                    viewModel.sortByInterestMatch = false
                    getUserLocation()
                    refresh()
                } else {
                    viewModel.sortByInterestMatch = true
                    refresh()

                }
            } else {

                startActivity(Intent(context, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }

        }


    }


    private fun privateModeSettings2() {

        var isTouched: Boolean = false
        var showDialog: Boolean = true
        var isUpdated: Boolean = true

        val ghostModeSettings = UserSessionManagement.getGhostMode()

        viewModel.privateModeState.observe(viewLifecycleOwner) {

            binding.imgPrivateModes.isChecked = it

//            refresh()

        }



        binding.imgPrivateModes.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, p1: MotionEvent): Boolean {
                isTouched = true;
                return false;
            }
        })

        binding.imgPrivateModes.setOnCheckedChangeListener { compoundButton, isChecked ->

            val ghostModeSettings = UserSessionManagement.getGhostMode()

            if (isTouched) {
                isTouched = false

                if (!ghostModeSettings) {

//                val token = UserSessionManagement.getKeyAuthToken()
//
//                if (token != null && !token.equals("")) {
                    if (!isChecked && showDialog) {

                        if (UserSessionManagement.isFromSettings() == false) {
                            ConfirmationDialog(
                                requireContext(),
                                getString(com.friendzrandroid.R.string.dialog_private_mode_message),
                                true
                            ).showDialog {
                                showDialog = it == 1
                                binding.imgPrivateModes.isChecked = it != 1

                                if (it == 1) {
                                    viewModel.updateUserPrivateMode(
                                        UserSessionManagement.getMyAppearanceType().toList(), false
                                    )
                                    UserSessionManagement.saveGhostMode(false)

                                    showDialog = true

                                } else !showDialog

                            }
                        } else {
                            UserSessionManagement.saveSettingsPrivateModeStatus(false)
                        }

                        Log.e("feed", "isChecked: false , $isChecked")
                        Log.e("feed", "ghostModeSettings: $ghostModeSettings")
                        Log.e("feed", "ghost mode false and it's in confirm dialog ")


                    } else if (isChecked && showDialog) {


                        AllowMyApperanceDialog(requireContext()).showDialog {

//                        Log.e("Feed", "onCreateView: $it")
//                        Log.e("Feed", "onCreateView: ${requireContext().packageName}")
                            //isUpdated = true

                            if (it.size == 0) {
                                showDialog = false
                                binding.imgPrivateModes.isChecked = false
                                showDialog = true


                                Log.e("feed", "isChecked: true")
                                Log.e("feed", "ghostModeSettings: $ghostModeSettings")
                                Log.e("feed", "ghost mode false and it == 0")
                                UserSessionManagement.saveGhostMode(false)
                            } else {

                                viewModel.updateUserPrivateMode(it, true)



                                Log.e("feed", "isChecked: true")
                                Log.e("feed", "ghostModeSettings: $ghostModeSettings")
                                Log.e(
                                    "feed", "ghost mode false and it != 0 and update private mode"
                                )

                            }
                        }


                    }
                } else {

                    if (isChecked == false) {

                        Log.e("feed", "isChecked: false")
                        Log.e("feed", "ghostModeSettings: $ghostModeSettings")
                        Log.e("feed", "else when ghost mode not open")


                        showDialog = true

                        ConfirmationDialog(
                            requireContext(),
                            getString(com.friendzrandroid.R.string.dialog_private_mode_message),
                            true
                        ).showDialog {
                            showDialog = it == 1
                            binding.imgPrivateModes.isChecked = it != 1

                            if (it == 1) {

                                viewModel.updateUserPrivateMode(
                                    UserSessionManagement.getMyAppearanceType().toList(), false
                                )
                                showDialog = true
                                UserSessionManagement.saveGhostMode(false)


                            } else !showDialog

                        }
                    } else {
                        Log.e("feed", "else to test")
                        Log.e("feed", "ghostModeSettings: $ghostModeSettings")


//                    AllowMyApperanceDialog(requireContext()).showDialog {
//
////                        Log.e("Feed", "onCreateView: $it")
////                        Log.e("Feed", "onCreateView: ${requireContext().packageName}")
//                        //isUpdated = true
//
//                        if (it.size == 0) {
//                            showDialog = false
//                            binding.imgPrivateModes.isChecked = false
//                            showDialog = true
//
//
//                            Log.e("feed", "isChecked: true")
//                            Log.e("feed", "ghostModeSettings: $ghostModeSettings")
//                            Log.e("feed", "ghost mode false and it == 0")
//                        } else {
//
//                            viewModel.updateUserPrivateMode(it, true)
//                            Log.e("feed", "isChecked: true")
//                            Log.e("feed", "ghostModeSettings: $ghostModeSettings")
//                            Log.e("feed", "ghost mode false and it != 0 and update private mode")
//
//
//
//                        }
//                    }


                    }

                }
            }


            //              }
//                else {
//
//                    startActivity(Intent(context, AuthActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    })
//                }

        }

    }

    private fun privateModeSettings() {

        var isTouched: Boolean = false
        var showDialog: Boolean = true
        var isUpdated: Boolean = true

        val ghostModeSettings = UserSessionManagement.getGhostMode()

        viewModel.privateModeState.observe(viewLifecycleOwner) {

            binding.imgPrivateModes.isChecked = it

//            refresh()

        }



        binding.imgPrivateModes.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, p1: MotionEvent): Boolean {
                isTouched = true;
                return false;
            }
        })

        binding.imgPrivateModes.setOnCheckedChangeListener { compoundButton, isChecked ->

            val ghostModeSettings = UserSessionManagement.getGhostMode()

            if (isTouched) {
                isTouched = false

                if (!ghostModeSettings) {

//                val token = UserSessionManagement.getKeyAuthToken()
//
//                if (token != null && !token.equals("")) {
                    if (!isChecked && showDialog) {

                        if (UserSessionManagement.isFromSettings() == false) {
                            ConfirmationDialog(
                                requireContext(),
                                getString(com.friendzrandroid.R.string.dialog_private_mode_message),
                                true
                            ).showDialog {
                                showDialog = it == 1
                                binding.imgPrivateModes.isChecked = it != 1

                                if (it == 1) {
                                    viewModel.updateUserPrivateMode(
                                        UserSessionManagement.getMyAppearanceType().toList(), false
                                    )
                                    UserSessionManagement.saveGhostMode(false)

                                    showDialog = true
                                } else !showDialog

                            }
                        } else {
                            UserSessionManagement.saveSettingsPrivateModeStatus(false)
                        }

                        Log.e("feed", "isChecked: false , $isChecked")
                        Log.e("feed", "ghostModeSettings: $ghostModeSettings")
                        Log.e("feed", "ghost mode false and it's in confirm dialog ")


                    } else if (isChecked && showDialog) {


                        AllowMyApperanceDialog(requireContext()).showDialog {

//                        Log.e("Feed", "onCreateView: $it")
//                        Log.e("Feed", "onCreateView: ${requireContext().packageName}")
                            //isUpdated = true

                            if (it.size == 0) {
                                showDialog = false
                                binding.imgPrivateModes.isChecked = false
                                showDialog = true


                                Log.e("feed", "isChecked: true")
                                Log.e("feed", "ghostModeSettings: $ghostModeSettings")
                                Log.e("feed", "ghost mode false and it == 0")
                                UserSessionManagement.saveGhostMode(false)
                            } else {

                                viewModel.updateUserPrivateMode(it, true)


                                Log.e("feed", "isChecked: true")
                                Log.e("feed", "ghostModeSettings: $ghostModeSettings")
                                Log.e(
                                    "feed", "ghost mode false and it != 0 and update private mode"
                                )

                            }
                        }


                    }
                } else {

                    if (isChecked == false) {

                        Log.e("feed", "isChecked: false")
                        Log.e("feed", "ghostModeSettings: $ghostModeSettings")
                        Log.e("feed", "else when ghost mode not open")


                        showDialog = true

                        ConfirmationDialog(
                            requireContext(),
                            getString(com.friendzrandroid.R.string.dialog_private_mode_message),
                            true
                        ).showDialog {
                            showDialog = it == 1
                            binding.imgPrivateModes.isChecked = it != 1

                            if (it == 1) {

                                viewModel.updateUserPrivateMode(
                                    UserSessionManagement.getMyAppearanceType().toList(), false
                                )
                                showDialog = true
                                UserSessionManagement.saveGhostMode(false)
                            } else !showDialog

                        }
                    } else {
                        Log.e("feed", "else to test")
                        Log.e("feed", "ghostModeSettings: $ghostModeSettings")


//                    AllowMyApperanceDialog(requireContext()).showDialog {
//
////                        Log.e("Feed", "onCreateView: $it")
////                        Log.e("Feed", "onCreateView: ${requireContext().packageName}")
//                        //isUpdated = true
//
//                        if (it.size == 0) {
//                            showDialog = false
//                            binding.imgPrivateModes.isChecked = false
//                            showDialog = true
//
//
//                            Log.e("feed", "isChecked: true")
//                            Log.e("feed", "ghostModeSettings: $ghostModeSettings")
//                            Log.e("feed", "ghost mode false and it == 0")
//                        } else {
//
//                            viewModel.updateUserPrivateMode(it, true)
//                            Log.e("feed", "isChecked: true")
//                            Log.e("feed", "ghostModeSettings: $ghostModeSettings")
//                            Log.e("feed", "ghost mode false and it != 0 and update private mode")
//
//
//
//                        }
//                    }


                    }

                }
            }


            //              }
//                else {
//
//                    startActivity(Intent(context, AuthActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    })
//                }

        }

    }

    private fun compassListener() {

        binding.compassButton.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (!isChecked) {
                binding.compassContainer.visibility = View.GONE
                viewModel.degree = 0
                viewModel.reload()
            } else {
                binding.compassButton.isChecked = true
                binding.compassContainer.visibility = View.VISIBLE
                registerSensorForCompass(true)
            }
        }


        binding.imageCompass.setOnClickListener {
            val token = UserSessionManagement.getKeyAuthToken()
            if (token != null && !token.equals("")) {
                //showToast(abs(currentDegree.roundToInt()).toString())
                viewModel.degree = abs(currentDegree.toInt())
                viewModel.reload()
            } else {
                startActivity(Intent(context, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }

        }
    }

    private fun getUserLocation() {

        var gpsTracker = GpsTracker(context)
        if (gpsTracker.canGetLocation()) {
            val latitude: Double = gpsTracker.getLatitude()
            val longitude: Double = gpsTracker.getLongitude()


            viewModel.userLong = longitude
            viewModel.userLat = latitude
        } else {
            gpsTracker.showSettingsAlert()
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.getSettings()
//        if (navigationView.visibility == View.GONE) {
//            navVnavigationViewiew.visibility = View.VISIBLE
//        } else {
//            var x = 0
//        }
//        locationUtils.subScribeReceiver()
        isActive = true

        if (binding.compassButton.isChecked) {
            registerSensorForCompass(true)
        } else {
            registerSensorForCompass(false)
        }



        binding.userImage.loadImage(UserSessionManagement.userImage())
        if (!this::recAdapter.isInitialized) {
            initRecyclerView()
        } else {
            recAdapter.reloadData()
        }


    }

    override fun onPause() {
        super.onPause()
        isActive = false
        registerSensorForCompass(false)
    }

    private fun registerSensorForCompass(needRegistered: Boolean) {

        if (!needRegistered) sensorManager.unregisterListener(this)
        else sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }


    private fun initRecyclerView() {

        getUserLocation()
        recAdapter = FeedsAdapter(viewModel, this)

        val bottomItemDecoration = ItemMarginBottomDecoration()
        binding.FeedRecycler.addItemDecoration(bottomItemDecoration)

        binding.FeedRecycler.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recAdapter.showLoading()

    }

    override fun onActionRequest(
        newStatus: RequestKeyStatus,
        position: Int,
        data: FeedRequestUserData,
        afterSuccessStatus: FeedKeyStatus
    ) {
        if (newStatus == RequestKeyStatus.MESSAGE) {
            val act = FeedFragmentDirections.actionNavigationFeedToNavigationChat(
                chatID = data.userId,
                chatImage = data.image,
                chatName = data.userName,
                chatIsEvent = false,
                myEvent = false,
                isFriend = true
            )
            findNavController().navigate(act)
        } else {
            lifecycleScope.launch {
                //showLoading()
                val isSuccess = viewModel.updateUserStatus(data.userId, newStatus.key)
                //hideLoading()
                if (isSuccess) {
                    getUserLocation()
                    viewModel.reload()
                }

            }
        }

    }

    override fun onStart() {
        super.onStart()


    }

    override fun onItemSelected(data: FeedRequestUserData) {
        val token = UserSessionManagement.getKeyAuthToken()


        if (token != null && !token.equals("")) {
            val action =
                FeedFragmentDirections.actionNavigationFeedToUserProfileFragment(data.userId)
            findNavController().navigate(action)
        } else {

            startActivity(Intent(context, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }


    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let { event ->

            val degree = event.values[0]
            val rotateAnimation = RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = 210
                fillAfter = true
            }

            binding.imageCompass.startAnimation(rotateAnimation)
            currentDegree = -degree

//            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
//                System.arraycopy(
//                    event.values,
//                    0,
//                    accelerometerReading,
//                    0,
//                    accelerometerReading.size
//                )
//            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
//                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
//            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //showToast("new value: ${p1}")
    }

    override fun onRefresh() {
        super.onRefresh()

        refresh()


    }

    fun refresh() {
        binding.FeedRecycler.show()
        binding.noDataFoundTxt.hide()
        binding.imgNoData.hide()
        recAdapter.showLoading()
        viewModel.reload()
    }


    private fun broadCastReceiver() {

        receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

                when (intent.action) {
                    "refresh-feed" -> {
                        getUserLocation()
                        viewModel.reload()
                    }

                    "update-request" -> {
                        var userId: String = ""
                        var key: Int = FeedKeyStatus.BASE_STATE.key

                        intent.extras?.let {
                            userId = it.getString("userId").toString()
                            key = it.getInt("key")
                        }

                        var indexToModify = -1
                        recAdapter.adapterList.forEachIndexed { index, userModel ->
                            if (userModel.data?.userId == userId) {
                                userModel.data?.key = key
                                indexToModify = index
                            }
                        }

                        if (key == FeedKeyStatus.OTHER_USER_BLOCKED_YOU.key && indexToModify != -1) recAdapter.deleteItem(
                            indexToModify
                        )
                        else if (indexToModify != -1) recAdapter.updateItem(indexToModify, key)

                    }
                }
                //recAdapter.reloadData()
            }
        }

        val intentFilter = IntentFilter("update-request")
        intentFilter.addAction("refresh-feed")

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, intentFilter)

    }


//    private var adView: MaxAdView? = null
//    private fun createBannerAd(){
//
//        binding.adBanner.loadAd()
//        binding.adBanner.startAutoRefresh()


    //ca-app-pub-6206027456764756/4483859663

//        adView = MaxAdView("a82067eb118aef35", requireContext())
//        adView?.setListener(this)
//
//        // Stretch to the width of the screen for banners to be fully functional
//        val width = ViewGroup.LayoutParams.MATCH_PARENT
//
//        // Banner height on phones and tablets is 50 and 90, respectively
//        val heightPx = 90
//
//        adView?.layoutParams = FrameLayout.LayoutParams(width, heightPx)
//
//        // Set background or background color for banners to be fully functional
//        //adView?.setBackgroundColor()
//
//        val rootView = binding.adBanner
//        rootView.addView(adView)

//        // Load the ad
//        adView?.loadAd()
//        adView?.startAutoRefresh()
//    }
}