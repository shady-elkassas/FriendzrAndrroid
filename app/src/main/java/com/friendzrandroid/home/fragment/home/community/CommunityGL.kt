package com.friendzrandroid.home.fragment.home.community

import android.R
import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.core.paggingList.BaseAdapterListener
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.FragmentCommunityBinding
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.MainViewModel
import com.friendzrandroid.home.adapter.CommunityRecentConnectionAdapter
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.community.RecentlyConnectedItemData
import com.friendzrandroid.home.data.model.community.RecommendedEventResponse
import com.friendzrandroid.home.data.model.community.RecommendedPeopleResponse
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import com.friendzrandroid.utils.AdsBannerUtil
import com.friendzrandroid.utils.ItemMarginEndDecoration
import com.friendzrandroid.utils.ProfileUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CommunityGL : BaseFragment(), BaseAdapterListener<RecentlyConnectedItemData> {


    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentCommunityBinding.inflate(layoutInflater)
    }
    val mainViewModel: MainViewModel by viewModels()

    private val viewModel: CommunityViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel

//    var noRecommendedConnection: Boolean = false
//    var noRecommendedEvent: Boolean = false

    private lateinit var communityRecentConnectionAdapter: CommunityRecentConnectionAdapter


    //handle requests badge
    // handle no network message
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        AdsBannerUtil.loadAds(binding.bannerAdView)

        binding.includeRecommendedConnection.shimmerLoading.startShimmer()
        binding.includeRecommendedEvents.shimmerLoading.startShimmer()
        viewModel.getRecommendedConnections("")
        viewModel.getRecommendedEvents("")
        setObservers()
        goToRequests()
        updateCurrentBadges()








        trackScreenName("My Hub Screen")
        return binding.root

    }


    private fun updateCurrentBadges() {
        if (UserSessionManagement.getRequestsNumber() > 0) {
            binding.tvCommunityRequestsBadge.show()
            binding.tvCommunityRequestsBadge.text =
                UserSessionManagement.getRequestsNumber().toString()
        } else {
            binding.tvCommunityRequestsBadge.hide()
        }

        mainViewModel.currentUserBadges.observe(requireActivity()) {
            when (it) {
                is DataState.NewData -> {

                    binding.tvCommunityRequestsBadge.text = it.data.frindRequestNumber.toString()

                }
                else -> {}
            }
        }


    }

    private fun goToRequests() {


        binding.tvRequestTitle.setOnClickListener {
            findNavController().navigate(com.friendzrandroid.R.id.navigation_requests)


        }
    }

    private fun setObservers() {

        viewModel.recommendedConnections.observe(viewLifecycleOwner) {
            when (it) {

                is DataState.NewData -> {
//                    hideLoading()
                    binding.noDataContainer.hide()

                    UserSessionManagement
                    setRecommendedConnectionData(it.data)

                }
                is DataState.FailData -> {
                    binding.noDataContainer.hide()

//                    showToast(it.message)
                    showErrorMessageForConnection(it.message)

                }
                else -> {

//                    binding.noDataContainer.hide()

                    showToast("Please try again")
                }
            }
        }
        viewModel.recommendedEvents.observe(viewLifecycleOwner) {
            when (it) {
//                is DataState.Loading -> showLoading()

                is DataState.NewData -> {
//                    hideLoading()¬
                    binding.noDataContainer.hide()

                    setRecommendedEventData(it.data)
                }
                is DataState.FailData -> {
//                    hideLoading()
                    binding.noDataContainer.hide()

                    showErrorMessageForEvents(it.message)
                }
                else -> {


                    showErrorMessageForEvents("Please try again")
                }

            }
        }
        viewModel.isErrorFound.observe(viewLifecycleOwner) {
            if (it) {
                binding.noDataContainer.show()
            } else {
                binding.noDataContainer.hide()


            }

        }
    }

    private fun showErrorMessageForEvents(message: String) {
        binding.includeRecommendedEvents.recommendationCardView.visibility = View.INVISIBLE
        binding.includeRecommendedEvents.btnSkipToNextRecommendationEvents.visibility =
            View.INVISIBLE

        binding.noDataFoundTxtForEvent.visibility = View.VISIBLE


        binding.noDataFoundTxtForEvent.text = message
    }

    private fun showErrorMessageForConnection(message: String) {
        binding.includeRecommendedConnection.rlRecommendedConnectedContainer.visibility =
            View.INVISIBLE
        binding.noDataFoundTxtForRecommendedConnection.visibility = View.VISIBLE

        binding.noDataFoundTxtForRecommendedConnection.text = message

    }

    private fun setRecommendedEventData(data: RecommendedEventResponse) {
        binding.includeRecommendedEvents.recommendationCardView.visibility = View.VISIBLE
        binding.includeRecommendedEvents.btnSkipToNextRecommendationEvents.visibility = View.VISIBLE

        binding.noDataFoundTxtForEvent.visibility = View.GONE

        data.eventtypecolor?.toColorInt()
            ?.let { binding.includeRecommendedEvents.eventTypeColor.setBackgroundColor(it) }
        binding.includeRecommendedEvents.imgEventImage.loadImage(data.image)
        binding.includeRecommendedEvents.eventTitle.text = data.title
        binding.includeRecommendedEvents.eventDescription.text = data.description

        binding.includeRecommendedEvents.eventAttendeceValue.text =
            "${data.attendees} / ${data.from}"

        binding.includeRecommendedEvents.eventDateValue.text = data.eventDate

//        val parent = binding.includeRecommendedEvents.recommendationEventsContainer
//        parent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)


        binding.includeRecommendedEvents.expandContainer.setOnClickListener {

//            isFromEventDetails = true


            val eventId = data.eventId
            val action = CommunityGLDirections.actionCommunityGLToEventDetailsFragment(eventId)
            findNavController().navigate(action)

        }
        binding.includeRecommendedEvents.imageContainer.setOnClickListener {

//            isFromEventDetails = true


            val eventId = data.eventId
            val action = CommunityGLDirections.actionCommunityGLToEventDetailsFragment(eventId)
            findNavController().navigate(action)

        }


        binding.includeRecommendedEvents.btnSkipToNextRecommendationEvents.setOnClickListener {


            viewModel.getRecommendedEvents(data.eventId)


//            binding.includeRecommendedEvents.recommendationEventsContainer.startAnimation(
//                AnimationUtils.loadAnimation(
//                    context,
//                    com.friendzrandroid.R.anim.slide_in_left_for_community
//                )
//            )

        }

        binding.includeRecommendedEvents.shimmerLoading.stopShimmer()
        binding.includeRecommendedEvents.shimmerLoading.hideShimmer()
    }


    private fun setRecommendedConnectionData(data: RecommendedPeopleResponse) {
        binding.includeRecommendedConnection.rlRecommendedConnectedContainer.visibility =
            View.VISIBLE
        binding.noDataFoundTxtForRecommendedConnection.visibility = View.GONE


        val interestMatchPercent = Math.round(data.interestMatchPercent)


        val distanceFromYouPercent = String.format("%.1f", data.distanceFromYou.toKm())


        binding.includeRecommendedConnection.tvUserName.text = data.name
        binding.includeRecommendedConnection.tvInterestsMatch.text =
            "$interestMatchPercent% interests match"
        binding.includeRecommendedConnection.tvRecommendationDistance.text =
            "$distanceFromYouPercent km from you"
        binding.includeRecommendedConnection.imgUserImage.loadImage(data.image)


        binding.includeRecommendedConnection.btnViewProfile.setOnClickListener {
            val userId = data.userId
            val action = CommunityGLDirections.actionCommunityGLToUserProfileFragment(data.userId)
            findNavController().navigate(action)
        }
        binding.includeRecommendedConnection.imgUserImage.setOnClickListener {
            val userId = data.userId
            val action = CommunityGLDirections.actionCommunityGLToUserProfileFragment(data.userId)
            findNavController().navigate(action)
        }

        binding.includeRecommendedConnection.btnSkipToNextRecommendation.setOnClickListener {

            viewModel.getRecommendedConnections(data.userId)


//call swipe animation
//            binding.includeRecommendedConnection.recommendationCardView.startAnimation(
//                AnimationUtils.loadAnimation(
//                    context,
//                    com.friendzrandroid.R.anim.slide_in_left_for_community
//                )
//            )


        }


        binding.includeRecommendedConnection.btnSendRequest.setOnClickListener {

            lifecycleScope.launch {
                //showLoading()
                val isSuccess = viewModel.updateUserStatus(data.userId, RequestKeyStatus.SEND.key)
                //hideLoading()
                if (isSuccess) {

                    viewModel.getRecommendedConnections(data.userId)


//                    binding.includeRecommendedConnection.recommendationCardView.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            context,
//                            com.friendzrandroid.R.anim.slide_in_left_for_community
//                        )
//                    )


                }

            }

        }


        if (data.matchedInterests != null && data.matchedInterests.size > 0) {
            binding.includeRecommendedConnection.communityUserEnjoyTags.show()

            binding.includeRecommendedConnection.noInterests.hide()

            ProfileUtil.addChipsViewsForCommunity(
                requireContext(), binding.includeRecommendedConnection.communityUserEnjoyTags, data

            )

            binding.includeRecommendedConnection.communityUserEnjoyTags.setOnClickListener {

                val userId = data.userId
                val action =
                    CommunityGLDirections.actionCommunityGLToUserProfileFragment(data.userId)
                findNavController().navigate(action)
            }


        } else {
            binding.includeRecommendedConnection.communityUserEnjoyTags.hide()

            binding.includeRecommendedConnection.noInterests.show()
        }

        binding.includeRecommendedConnection.shimmerLoading.stopShimmer()
        binding.includeRecommendedConnection.shimmerLoading.hideShimmer()

    }

    private fun initRecyclerView() {


        communityRecentConnectionAdapter = CommunityRecentConnectionAdapter(viewModel, this)

        binding.rcRecentConnectedList.apply {
            adapter = communityRecentConnectionAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            //         LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        val endItemDecoration = ItemMarginEndDecoration()
        binding.rcRecentConnectedList.addItemDecoration(endItemDecoration)

//        communityRecentConnectionAdapter.reloadData()
        communityRecentConnectionAdapter.showLoadingHorizontal()

        recentConnectedVisbility()
    }

    private fun recentConnectedVisbility() {


        binding.rcRecentConnectedList.show()


        viewModel.totalItemCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.rcRecentConnectedList.show()

                binding.noDataFoundTxt.hide()
            } else {

                binding.rcRecentConnectedList.hide()
                binding.noDataFoundTxt.show()
            }
        }

    }


    override fun onItemSelected(data: RecentlyConnectedItemData) {


        val userId = data.userId
        val action = CommunityGLDirections.actionCommunityGLToUserProfileFragment(data.userId)
        findNavController().navigate(action)

    }


    override fun onResume() {
        super.onResume()
        viewModel.pageNumber = 1

        if (!this::communityRecentConnectionAdapter.isInitialized) {

            initRecyclerView()
        } else {
            communityRecentConnectionAdapter.reloadData()
        }
    }
}
