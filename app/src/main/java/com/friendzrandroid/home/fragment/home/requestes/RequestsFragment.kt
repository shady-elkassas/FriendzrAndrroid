package com.friendzrandroid.home.fragment.home.requestes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.RequestsFragmentBinding
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.adapter.RequestsAdapter
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import com.friendzrandroid.utils.AdsBannerUtil
import com.friendzrandroid.utils.ItemMarginBottomDecoration
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RequestsFragment : BaseFragment(), FeedRequestAdapterListener<FeedRequestUserData> {

    companion object {
        var isActive: Boolean = false
    }

    private lateinit var receiver: BroadcastReceiver

    private val TAG = "RequestsFragment"

    private val viewModel: RequestsViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        RequestsFragmentBinding.inflate(layoutInflater)
    }


    private lateinit var recAdapter: RequestsAdapter
    private var currentTabPosition = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        AdsBannerUtil.loadAds(binding.bannerAdView)

        broadCastReceiver()

        binding.swipeToRefreshRequests.changeColor()

        viewModel.totalItemCount.observe(viewLifecycleOwner) {

            binding.swipeToRefreshRequests.isRefreshing = false

//            if (currentTabPosition == 2)
//                updateBadge(it)

            if (it > 0) {
                binding.requestsRecycler.show()
                binding.noDataContainer.hide()
                //binding.noDataFoundTxt.hide()
            } else {

                if (viewModel.currentTab == 2)
                    binding.noDataFoundTxt.text =
                        resources.getString(R.string.no_data_request)
                else
                    binding.noDataFoundTxt.text =
                        resources.getString(R.string.title_no_data_request_sent)


                binding.requestsRecycler.hide()
                binding.noDataContainer.show()
                //binding.noDataFoundTxt.show()
            }
            //binding.totalRequestsValue.text = "${it}"
        }


        binding.userImage.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.tabRequestType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.i(TAG, "onTabSelected: ${tab?.position}")
                currentTabPosition = if (tab?.position == 0) 2 else 1
                viewModel.currentTab = currentTabPosition
                recAdapter.reloadData()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.swipeToRefreshRequests.setOnRefreshListener {
            recAdapter.reloadData()
        }


        trackScreenName("Requests Screen")
        return binding.root

    }


    override fun onResume() {
        super.onResume()
        binding.userImage.loadImage(UserSessionManagement.userImage())

        isActive = true
        viewModel.currentTab = 2

        if (!this::recAdapter.isInitialized) {
            initRecyclerView()
        } else {
            viewModel.currentTab = currentTabPosition
            //viewModel.reload()
            recAdapter.reloadData()
        }
    }

    private fun initRecyclerView() {

        viewModel.pageNumber = 1
        recAdapter = RequestsAdapter(viewModel, this, true)

        val bottomItemDecoration = ItemMarginBottomDecoration()
        binding.requestsRecycler.addItemDecoration(bottomItemDecoration)

        binding.requestsRecycler.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        recAdapter.showLoading()
    }

//    private fun updateBadge(badgeNumber: Int) {
//        (activity as MainActivity).showBadge(badgeNumber)
//    }


    override fun onActionRequest(
        newStatus: RequestKeyStatus,
        position: Int,
        data: FeedRequestUserData,
        afterSuccessStatus: FeedKeyStatus
    ) {

        if (newStatus == RequestKeyStatus.MESSAGE) {

            val act = RequestsFragmentDirections.actionNavigationRequestsToNavigationChat(
                chatID = data.userId,
                chatImage = data.image,
                chatName = data.userName,
                chatIsEvent = false,
                myEvent = false,
                isFriend = true
            )
            findNavController().navigate(act)

        } else

            lifecycleScope.launch {
                //showLoading()
                val isSuccess = viewModel.updateUserStatus(data.userId, newStatus.key)
                //hideLoading()
                if (isSuccess) {
                    data.key = newStatus.key
                    recAdapter.updateItem(position, afterSuccessStatus.key)
                    val currentRequestsNumber = UserSessionManagement.getRequestsNumber()
                    val updatedRequestsNumber = currentRequestsNumber - 1
                    UserSessionManagement.updateFriendRequestNumber(updatedRequestsNumber)
                    (activity as MainActivity)
                        .showFriendRequestBadge(updatedRequestsNumber)
                }

            }
    }


    override fun onItemSelected(data: FeedRequestUserData) {
        val action =
            RequestsFragmentDirections.actionNavigationFeedToUserProfileFragment(data.userId)
        findNavController().navigate(action)
    }

    private fun broadCastReceiver() {

        receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                recAdapter.reloadData()
            }
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiver, IntentFilter("update-request"))

    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }


}