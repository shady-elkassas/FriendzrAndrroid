package com.friendzrandroid.home.fragment.home.events.eventDetails


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.facebook.shimmer.ShimmerFrameLayout
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.EventDetailsFragmentBinding
import com.friendzrandroid.home.adapter.listener.TicketDetailsListener
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.data.model.enum.CickUserFromEventEnum
import com.friendzrandroid.home.data.model.enum.EventStates
import com.friendzrandroid.home.data.model.enum.ReportStates
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.dialog.SeeMoreDialog.DescriptionSeeMoreDialog
import com.friendzrandroid.home.domain.model.LeaveEventRequest
import com.friendzrandroid.utils.AdsBannerUtil
import com.friendzrandroid.utils.HelperClass.showWebViewExternalEventDialogScreen
import com.friendzrandroid.utils.MenuUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EventDetailsFragment : BaseFragment(), TicketDetailsListener {

    private val TAG = "EventDetailsFragment"

    val args: EventDetailsFragmentArgs by navArgs()


    val eventID by lazy {
        Log.e("eventDetails", args.eventID)
        args.eventID
    }

    private lateinit var eventData: EventDetails
    private var isMyEvent: Boolean = false

    private val viewModel: EventDetailsViewModel by viewModels()


    val binding by lazy(LazyThreadSafetyMode.NONE) {
        EventDetailsFragmentBinding.inflate(layoutInflater)

    }
    override val baseViewModel: BaseViewModel
        get() = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        AdsBannerUtil.loadAds(binding.bannerAdView)

        binding.ShimmerEventView.startShimmer()
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


        trackScreenName()


        return binding.root
    }

    private fun trackScreenName() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Event Screen")
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeToRefreshEventDetails.changeColor()

        viewModel.getEventDetails(eventID)

        binding.seeMoreContianer.setOnClickListener {
            findNavController().navigate(
                EventDetailsFragmentDirections.actionShowAllAttendance(
                    eventID = eventID,
                    isEventAdmin = isMyEvent
                )
            )
        }

        viewModel.actionToUser.observe(viewLifecycleOwner) {
            when (it) {
                CickUserFromEventEnum.REMOVE.value -> {
                    showToast("Account is removed")
                }

                CickUserFromEventEnum.BLOCK.value -> {
                    showToast("Account is blocked")

                }
            }

        }

        viewModel.eventDetails.observe(viewLifecycleOwner) {
            binding.swipeToRefreshEventDetails.isRefreshing = false
            binding.ShimmerEventView.hideShimmer()
            when (it) {
                is DataState.NewData -> {
                    eventData = it.data
                    setEventDetails(it.data)
                }
                is DataState.FailData -> {
                    showToast(it.message)
                }
                else -> {}
            }
        }

//        viewModel.loading.observe(viewLifecycleOwner) {
//            if (it) {
//                //showLoading()
//
//            } else {
//                //hideLoading()
//            }
//        }

        binding.actionBtn.apply {
            setOnClickListener {
                when (it.tag) {

                    resources.getString(R.string.title_join) -> {

                        if (eventData.eventTypeName != "External") {

                            joinTheEvent()

                        } else {

                            activity?.let {
                                showWebViewExternalEventDialogScreen(
                                    it,
                                    eventData.checkout_details,
                                    resources.getString(R.string.title_ticket_details),
                                    this@EventDetailsFragment
                                )
                            }
                        }
                    }

                    resources.getString(R.string.title_editEvent) -> {
                        findNavController().navigate(
                            EventDetailsFragmentDirections.actionEventDetailsFragmentToEditEventFragment(
                                eventID
                            )
                        )
                    }

                    resources.getString(R.string.title_leaveEvent) -> {

                        val currentDate =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                        val currentTime =
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                        Log.e(
                            TAG,
                            "onViewCreated: ${LeaveEventRequest(eventID, currentDate, currentTime)}"
                        )

                        showButtonLoading(resources.getString(R.string.title_leaving))
                        viewModel.leaveEvent(
                            LeaveEventRequest(eventID, currentDate, currentTime)
                        )
                    }
                }
            }
        }
//        binding.actionBtn.setOnClickListener {
//            when (it.tag) {
//                resources.getString(R.string.title_join) -> {
//                    viewModel.joinEvent(eventID)
//                }
//                resources.getString(R.string.title_editEvent) -> {
//                    findNavController().navigate(
//                        EventDetailsFragmentDirections.actionEventDetailsFragmentToEditEventFragment(
//                            eventID
//                        )
//                    )
//                }
//                resources.getString(R.string.title_leaveEvent) -> {
//
//                    val currentDate =
//                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
//                    val currentTime =
//                        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
//
//                    Log.e(
//                        TAG,
//                        "onViewCreated: ${LeaveEventRequest(eventID, currentDate, currentTime)}"
//                    )
//
//                    viewModel.leaveEvent(
//                        LeaveEventRequest(eventID, currentDate, currentTime)
//                    )
//                }
//            }
//        }

        binding.swipeToRefreshEventDetails.setOnRefreshListener {
            binding.ShimmerEventView.startShimmer()
            viewModel.getEventDetails(eventID)
        }

        binding.imgProfileMenu.setOnClickListener {
            MenuUtil(requireActivity(), it, R.menu.event_menu).showMenu { selectedMenuId ->
                when (selectedMenuId) {
                    R.id.report -> {
                        val action =
                            EventDetailsFragmentDirections.actionEventDetailsFragmentToReportFragment(
                                eventID,
                                ReportStates.REPORT_EVENT.value
                            )
                        findNavController().navigate(action)
                    }

                    R.id.share -> {
                        val action =
                            EventDetailsFragmentDirections.actionEventDetailsFragmentToEventShareFragment(
                                eventID,
                                binding.eventTitleValue.text.toString()
                            )
                        findNavController().navigate(action)
                    }
                }
                return@showMenu true
            }
        }
    }


    private fun setEventDetails(data: EventDetails) {


        isMyEvent = data.key == EventStates.MINE.key

        binding.tvInboxTitle.text = "${data.eventTypeName} Event"
        binding.eventImage.loadImage(data.image)

        if (eventData.eventTypeName == "External") {
            binding.eventTitleValue.hide()
            binding.tvEventCategory.hide()
            binding.eventTitleValueExternal.text = data.title
            binding.eventTitleValueExternal.show()

            if (data.categorie != null) {
                binding.eventCategoryValueExternal.text = data.categorie
                binding.eventCategoryValueExternal.show()
            }

            binding.externalEventDataContainer.show()

        } else {
            binding.eventTitleValueExternal.hide()
            binding.eventTitleValue.text = data.title
        }

        binding.eventDateValue.text =
            "${data.eventdate} To: ${data.eventdateto}"

        binding.eventTimeValue.text =
            if (data.allday) data.timetext else "${data.timefrom} To: ${data.timeto}"

        binding.eventAttendenceNumbers.setText("${resources.getString(R.string.title_attendance)} ${data.joined} / ${data.totalnumbert}")
        binding.tvEventCategory.text = data.categorie
        binding.eventDescriptionValue.text = data.description

        Log.e(TAG, "setEventDetails: ${binding.eventDescriptionValue.lineCount}")

        if (binding.eventDescriptionValue.lineCount >= 3) {
            binding.eventDescriptionSeeMore.show()
            binding.eventDescriptionSeeMore.setOnClickListener {
                DescriptionSeeMoreDialog(requireContext(), data.description).showDialog {

                }
            }
        } else
            binding.eventDescriptionSeeMore.hide()

        if (data.genderStatistic != null && data.genderStatistic.size > 0) {
            setGenderStatics(data.genderStatistic)
        }


        if (data.interestStatistic != null && data.interestStatistic.size > 0) {
            setInterestsStatics(data.interestStatistic)
        }



        showEventMap(data)


        binding.eventDetailsChatButton.apply {
            show()
            setOnClickListener { gotToEventChat() }
        }

        if (data.eventHasExpired) {
            binding.actionBtn.visibility = View.INVISIBLE
            binding.imgProfileMenu.visibility = View.INVISIBLE


        } else {
            eventActionButtonFunctionality(data)


        }





        binding.eventButtonsContainer.visibility = View.VISIBLE

        if (data.attendees != null && data.attendees.size > 0) {
            setEventAttendese(data.attendees.sortedByDescending { it.myEvent })
        }
    }

    private fun eventActionButtonFunctionality(data: EventDetails) {
        binding.actionBtn.visibility = View.VISIBLE
        binding.imgProfileMenu.visibility = View.VISIBLE

        binding.actionBtn.show()
        if (data.key == EventStates.MINE.key) {


            binding.actionBtn.setText(resources.getString(R.string.title_editEvent))
            binding.actionBtn.tag = resources.getString(R.string.title_editEvent)
            binding.actionBtn.setBackgroundResource(R.drawable.shape_send_request)



            binding.eventExternalHint.hide()

        } else if (data.key == EventStates.JOINED.key) {
            binding.actionBtn.setText(resources.getString(R.string.title_leaveEvent))
            binding.actionBtn.tag = resources.getString(R.string.title_leaveEvent)
            binding.actionBtn.setBackgroundResource(R.drawable.shape_red)
            binding.eventDetailsChatButton.apply {
                show()
                setOnClickListener { gotToEventChat() }
            }

            binding.eventExternalHint.hide()

        } else {
            binding.actionBtn.setText(resources.getString(R.string.title_join))
            binding.actionBtn.tag = resources.getString(R.string.title_join)
            binding.actionBtn.setBackgroundResource(R.drawable.shape_send_request)
            binding.eventDetailsChatButton.hide()

            if (eventData.eventTypeName == "External") {
                binding.eventExternalHint.show()
                binding.eventAttendenceNumbers.hide()
                binding.friendzrAttendeeTitle.show()
            }
        }

    }

    private fun setEventAttendese(data: List<AttendenceData>) {
        binding.eventAttendeceList.removeAllViews()
        if (data.size == 0) {
            binding.eventAttendecesContainer.hide()
        } else if (data.size == 1) {
            binding.eventAttendecesContainer.show()

            binding.seeMoreContianer.hide()
            val view =
                (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    R.layout.item_event_attendece, null
                )

            isAdmin(view, data[0].myEvent)

            view.findViewById<CircleImageView>(R.id.eventUserImage).loadImage(data.get(0).image)
            view.findViewById<ShimmerFrameLayout>(R.id.shimmerLoading).hideShimmer()
            view.findViewById<ImageView>(R.id.imageViewMenu).setOnClickListener {
                showOptionMenu(it, data[0].userId)
            }

            view.findViewById<TextView>(R.id.eventUserName).setText(data.get(0).userName)
            binding.eventAttendeceList.addView(view)

        } else {
            binding.eventAttendecesContainer.show()

            binding.seeMoreContianer.show()
            val visItems = data.size / 2
            for (pos in 0..visItems) {
                val view =
                    (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                        R.layout.item_event_attendece, null
                    )

                isAdmin(view, data[pos].myEvent)

                view.findViewById<CircleImageView>(R.id.eventUserImage)
                    .loadImage(data.get(pos).image)
                view.findViewById<TextView>(R.id.eventUserName).setText(data.get(pos).userName)
                view.findViewById<ShimmerFrameLayout>(R.id.shimmerLoading).hideShimmer()
                view.findViewById<ImageView>(R.id.imageViewMenu).setOnClickListener {
                    showOptionMenu(view, data[pos].userId)
                }
                binding.eventAttendeceList.addView(view)
            }

        }
    }

    private fun isAdmin(view: View, isAdmin: Boolean) {
        if (isAdmin) {
            view.findViewById<ImageView>(R.id.imageViewMenu).hide()
            view.findViewById<TextView>(R.id.eventAdminText).show()
        } else {
            if (!isMyEvent)
                view.findViewById<LinearLayout>(R.id.actionBtnContainer).hide()
        }
    }

    private fun showOptionMenu(view: View, userId: String) {
        Log.i(TAG, "showOptionMenu: UserId: $userId")
        MenuUtil(
            requireActivity(),
            view,
            R.menu.user_event_actions
        ).showMenu { selectedMenuItem ->
            when (selectedMenuItem) {
                R.id.remove -> viewModel.clickUserEvent(
                    eventID,
                    userId,
                    CickUserFromEventEnum.REMOVE
                )
                R.id.block -> viewModel.clickUserEvent(eventID, userId, CickUserFromEventEnum.BLOCK)
            }
            return@showMenu true
        }
    }

//    private fun setChartData(data: List<InterestStatistic>, joind: Int) {
//
//        val items = ArrayList<DonutSection>()
//        for (item in data) {
//            val rnd = Random()
//            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//
//            items.add(
//                DonutSection(
//                    name = item.name,
//                    color = color,
//                    amount = item.count.toFloat()
//                )
//            )
//
//        }
//
//        binding.eventInterestsChart.cap = joind.toFloat()
//        try {
//            binding.eventInterestsChart.submitData(items)
//        } catch (e: Exception) {
//
//        }
//    }

    private fun setGenderStatics(data: List<GenderStatistic>) {

        for (currentData in data) {
            when (currentData.key) {
                "Male" -> {
                    binding.tvMaleItemValue.text = "${currentData.count} %"
                    binding.maleProgressBar.progress = currentData.count
                }

                "Female" -> {
                    binding.tvFemaleItemValue.text = "${currentData.count} %"
                    binding.femaleProgressBar.progress = currentData.count
                }

                else -> {
                    binding.tvOtherGenderItemValue.text = "${currentData.count} %"
                    binding.otherGenderProgressBar.progress = currentData.count
                }
            }
        }
    }

    private fun setInterestsStatics(data: List<InterestStatistic>) {
        binding.firstInterestItemTitle.text = data[0].name
        binding.firstInterestItemValue.text = "${data[0].count} %"
        binding.firstInterestProgressBar.progress = data[0].count

        binding.secondInterestItemTitle.text = data[1].name
        binding.secondInterestItemValue.text = "${data[1].count} %"
        binding.secondInterestProgressBar.progress = data[1].count

        binding.thirdInterestItemTitle.text = data[2].name
        binding.thirdInterestItemValue.text = "${data[2].count} %"
        binding.thirdInterestProgressBar.progress = data[2].count
    }

    private fun showEventMap(data: EventDetails) {
        val basicUrl = "https://maps.googleapis.com/maps/api/staticmap?"

        val mapKey = "&key=${getString(R.string.api_key)}"
        val locationOnMap = "center=${data.lat},${data.lang}"
        val urlParams = "&zoom=16&size=500x250&markers=color:red%7C|${data.lat},${data.lang}"

        val mapUrl = basicUrl + locationOnMap + urlParams + mapKey
        val showMapDirection =
            "https://www.google.com/maps/dir/?api=1&origin=${getUserLocation()}&destination=${data.lat},${data.lang}&travelmode=driving"

        binding.staticMapImageView.loadImage(Uri.parse(mapUrl))


        binding.directionButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(showMapDirection))
            startActivity(intent)
        }
    }

    private fun getUserLocation(): String {
        val gpsTracker = GpsTracker(context)
        if (gpsTracker.canGetLocation())
            return "${gpsTracker.latitude},${gpsTracker.longitude}"
        else
            gpsTracker.showSettingsAlert()

        return ""
    }

    private fun joinTheEvent() {
        binding.actionBtn.showButtonLoading(resources.getString(R.string.title_joining))
        viewModel.joinEvent(eventID)
    }

    private fun gotToEventChat() {
        val action =
            EventDetailsFragmentDirections.actionEventDetailsFragmentToNavigationChat(
                chatID = eventID,
                chatImage = eventData.image,
                chatName = eventData.title,
                chatIsEvent = true,
                myEvent = eventData.key == EventStates.JOINED.key,
                isFriend = false,
                chatIsGroup = false
            )
        findNavController().navigate(action)
    }

    override fun onTicketWebClosed(isClosed: Boolean) {
        if (isClosed) {
            ConfirmationDialog(
                requireContext(),
                resources.getString(R.string.title_complete_event_ticket_dialog),
                true
            ).showDialog {
                if (it == 1) joinTheEvent()
            }
        }
    }

//    private fun setGenderData(data: List<GenderStatistic>) {
//        for (item in data) {
//            val view =
//                (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
//                    R.layout.evenet_gender_item, null
//                )
//
//            if (item.key.lowercase().equals("male")) {
//                view.findViewById<CardView>(R.id.itemColor).setCardBackgroundColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.color_male
//                    )
//                )
//            } else if (item.key.lowercase().equals("female")) {
//                view.findViewById<CardView>(R.id.itemColor).setCardBackgroundColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.color_female
//                    )
//                )
//            } else {
//
//                view.findViewById<CardView>(R.id.itemColor).setCardBackgroundColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.color_other
//                    )
//                )
//
//            }
//
//            view.findViewById<TextView>(R.id.itemTitle).setText(item.key)
//            view.findViewById<TextView>(R.id.itemValue).setText(item.count.toString())
//
//            view.layoutParams = RelativeLayout.LayoutParams(
//                MATCH_PARENT,
//                resources.getDimensionPixelSize(R.dimen.d_50)
//            ).apply {
//                topMargin = resources.getDimension(R.dimen.d_5).toInt()
//                bottomMargin = resources.getDimension(R.dimen.d_5).toInt()
//            }
//            val sep = LinearLayout(requireContext())
//            val params = LinearLayout.LayoutParams(
//                MATCH_PARENT,
//                resources.getDimensionPixelSize(R.dimen.d_1)
//            )
//            params.topMargin = resources.getDimension(R.dimen.d_5).toInt()
//            params.bottomMargin = resources.getDimension(R.dimen.d_5).toInt()
//
//            sep.layoutParams = params
//
//
//            sep.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.view_bg))
//
//            binding.genderStatistic.addView(view)
//            binding.genderStatistic.addView(sep)
//
//        }
//        binding.genderStatistic.removeView(binding.genderStatistic.allViews.last())
//    }

}