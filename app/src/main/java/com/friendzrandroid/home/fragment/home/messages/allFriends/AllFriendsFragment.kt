package com.friendzrandroid.home.fragment.home.messages.allFriends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.AllUsersFragmentBinding
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.adapter.MyFriendsAdapter
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AllFriendsFragment : BaseFragment(), FeedRequestAdapterListener<FeedRequestUserData> {


    private lateinit var recAdapter: MyFriendsAdapter


    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        AllUsersFragmentBinding.inflate(layoutInflater)
    }

    val viewModel: AllFriendsViewModel by viewModels()


    override val baseViewModel: BaseViewModel
        get() = viewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        handleSearchFriends()

        viewModel.totalItemCount.observe(viewLifecycleOwner) {

            if (it > 0) {
                binding.FeedRecycler.show()
                binding.noDataContainer.hide()
                binding.tvInboxCreateConversation.show()
            } else {
                binding.FeedRecycler.hide()
                binding.noDataContainer.show()
                binding.tvInboxCreateConversation.hide()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvInboxCreateConversation.setOnClickListener {
            val action = AllFriendsFragmentDirections.actionMyFriendsFragmentToCreateGroupFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (!this::recAdapter.isInitialized) {
            initRecyclerView()
        } else {
            recAdapter.reloadData()
        }
    }

    private fun initRecyclerView() {

        recAdapter = MyFriendsAdapter(viewModel, this)


        binding.FeedRecycler.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recAdapter.showLoading()

    }


    private fun handleSearchFriends() {

        binding.searchEdt.doOnTextChanged { text, start, before, count ->
            if (before == 1)
                text?.let {
                    if (it.isEmpty()) {
                        viewModel.searchText = null
                        viewModel.pageNumber = 1
                        recAdapter.reloadData()
                    }
                }
        }

        binding.searchEdt.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val txt = binding.searchEdt.text.toString()
                if (txt.isNotEmpty()) {
                    viewModel.searchText = txt
                    recAdapter.reloadData()
                } else {
                    recAdapter.reloadData()
                }
            }

            return@setOnEditorActionListener false
        }
    }


    override fun onItemSelected(data: FeedRequestUserData) {

    }

    override fun onActionRequest(
        newStatus: RequestKeyStatus,
        position: Int,
        data: FeedRequestUserData,
        afterSuccessStatus: FeedKeyStatus
    ) {
//       val userInbox =  InboxData(data.userId,data.image,false,"","","",false,data.displayedUserName)
        val action = AllFriendsFragmentDirections.actionFriendsFragmentToChatFragment(
            chatID = data.userId,
            chatImage = data.image,
            chatName = data.userName,
            isFriend = true,
            chatIsGroup = false,
            chatIsEvent = false
        )
        findNavController().navigate(action)
    }
}