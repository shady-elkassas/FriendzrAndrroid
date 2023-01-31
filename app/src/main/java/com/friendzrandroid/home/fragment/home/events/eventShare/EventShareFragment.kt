package com.friendzrandroid.home.fragment.home.events.eventShare

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.FragmentShareBinding
import com.friendzrandroid.home.adapter.EventShareAdapter
import com.friendzrandroid.home.adapter.listener.EventShareAdapterListener
import com.friendzrandroid.home.adapter.EventShareFriendAdapter
import com.friendzrandroid.home.adapter.EventShareGroupAdapter
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.enum.MessageTypeEnum
import com.friendzrandroid.home.data.model.enum.ShareTypeState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventShareFragment : Fragment(), EventShareAdapterListener {

    private val TAG = "EventShareFragment"

    private val viewModel: EventShareViewModel by viewModels()
    private val viewModelFriends: EventShareFriendViewModel by viewModels()
    private val viewModelGroups: EventShareGroupViewModel by viewModels()

    val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentShareBinding.inflate(layoutInflater)
    }

    val args: EventShareFragmentArgs by navArgs()
    val eventId by lazy {
        args.eventId
    }
    val message by lazy {
        args.message
    }

    var positionToChange: Int = 0

    lateinit var eventShareAdapter: EventShareAdapter
    lateinit var eventShareFriendAdapter: EventShareFriendAdapter
    lateinit var eventShareGroupAdapter: EventShareGroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!this::eventShareAdapter.isInitialized) {
            eventShareAdapter = EventShareAdapter(viewModel, requireActivity(), this)
            eventShareAdapter.showLoading()
        }

        if (!this::eventShareFriendAdapter.isInitialized) {
            eventShareFriendAdapter =
                EventShareFriendAdapter(viewModelFriends, requireActivity(), this)
            eventShareFriendAdapter.showLoading()
        }

        if (!this::eventShareGroupAdapter.isInitialized) {
            eventShareGroupAdapter =
                EventShareGroupAdapter(viewModelGroups, requireActivity(), this)
            eventShareGroupAdapter.showLoading()
        }

        binding.shareToEventsRecycler.apply {
            adapter = eventShareAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.shareToFriendsRecycler.apply {
            adapter = eventShareFriendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.shareToGroupsRecycler.apply {
            adapter = eventShareGroupAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        hideRecycler()

        viewModel.loadData()
        viewModel.totalItemCount.observe(viewLifecycleOwner) {
            Log.i(TAG, "onViewCreated: Event: $it")

            if (it > 0) {
                binding.progressEvent.hide()
                binding.shareToEventsRecycler.show()
            } else {
                binding.progressEvent.hide()
                binding.noDataEvent.show()
            }
        }
        viewModel.messageSendState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    //eventShareAdapter.isShareDone = true
                    eventShareAdapter.updateButtonState(positionToChange, true)
                }

                else -> {
                    //eventShareAdapter.isShareDone = false
                    eventShareAdapter.updateButtonState(positionToChange, false)
                }
            }
        }



        handleShareToFriend()
        handleShareToGroup()


        handleFriendsSearch()
        handleEventSearch()
        handleGroupSearch()
    }

    private fun hideRecycler() {
        binding.apply {
            shareToFriendsRecycler.hide()
            shareToEventsRecycler.hide()
            shareToGroupsRecycler.hide()

            progressFriends.show()
            progressEvent.show()
            progressGroup.show()
        }
    }

    private fun handleFriendsSearch() {
        binding.shareToFriendSearch.doAfterTextChanged {
            it?.let {
                if (it.isEmpty()) {
                    viewModelFriends.searchText = null
                    eventShareFriendAdapter.reloadData()
                }
            }
        }

        binding.shareToFriendSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val txt = binding.shareToFriendSearch.text.toString()
                if (txt.isNotEmpty()) {
                    viewModelFriends.searchText = txt
                    eventShareFriendAdapter.reloadData()
                } else {
                    eventShareFriendAdapter.reloadData()
                }
            }

            return@setOnEditorActionListener false
        }
    }

    private fun handleShareToFriend() {
        viewModelFriends.loadData()
        viewModelFriends.totalItemCount.observe(viewLifecycleOwner) {
            Log.i(TAG, "onViewCreated: $it")

            if (it > 0) {
                binding.progressFriends.hide()
                binding.shareToFriendsRecycler.show()
            } else {
                binding.progressFriends.hide()
                binding.noDataFriends.show()
            }
        }

        viewModelFriends.messageSendState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    //eventShareFriendAdapter.isShareDone = true
                    eventShareFriendAdapter.updateButtonState(positionToChange, true)
                }

                is DataState.FailData -> {
                    //eventShareFriendAdapter.isShareDone = false
                    eventShareFriendAdapter.updateButtonState(positionToChange, false)
                }
                else -> {}
            }
        }
    }


    private fun handleGroupSearch() {
        binding.shareToGroupsSearch.doAfterTextChanged {
            it?.let {
                if (it.isEmpty()) {
                    viewModelGroups.searchText = null
                    eventShareGroupAdapter.reloadData()
                }
            }
        }

        binding.shareToGroupsSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val txt = binding.shareToGroupsSearch.text.toString()
                if (txt.isNotEmpty()) {
                    viewModelGroups.searchText = txt
                    eventShareGroupAdapter.reloadData()
                } else {
                    eventShareGroupAdapter.reloadData()
                }
            }

            return@setOnEditorActionListener false
        }
    }

    private fun handleShareToGroup() {
        viewModelGroups.loadData()
        viewModelGroups.totalItemCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.progressGroup.hide()
                binding.shareToGroupsRecycler.show()
            } else {
                binding.noDataGroup.show()
                binding.progressGroup.hide()
            }
        }

        viewModelGroups.messageSendState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    //eventShareGroupAdapter.isShareDone = true
                    eventShareGroupAdapter.updateButtonState(positionToChange, true)
                }

                is DataState.FailData -> {
                    //eventShareGroupAdapter.isShareDone = false
                    eventShareGroupAdapter.updateButtonState(positionToChange, false)
                }
                else -> {}
            }
        }
    }


    private fun handleEventSearch() {
        binding.shareToEventsSearch.doAfterTextChanged {
            it?.let {
                if (it.isEmpty()) {
                    viewModel.searchText = null
                    eventShareAdapter.reloadData()
                }
            }
        }

        binding.shareToEventsSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val txt = binding.shareToEventsSearch.text.toString()
                if (txt.isNotEmpty()) {
                    viewModel.searchText = txt
                    eventShareAdapter.reloadData()
                } else {
                    eventShareAdapter.reloadData()
                }
            }

            return@setOnEditorActionListener false
        }
    }


    override fun onSendButtonPressed(
        idToShareTo: String,
        shareToState: ShareTypeState,
        position: Int
    ) {

        positionToChange = position

        when (shareToState) {
            ShareTypeState.SHARE_TO_FRIEND -> {
                viewModelFriends.shareToFriend(
                    eventId,
                    message,
                    MessageTypeEnum.SHARE.value,
                    idToShareTo
                )
            }

            ShareTypeState.SHARE_TO_EVENT -> {
                viewModel.shareToEvent(eventId, message, MessageTypeEnum.SHARE.value, idToShareTo)
            }

            ShareTypeState.SHARE_TO_GROUP -> {
                viewModelGroups.shareToGroup(
                    eventId,
                    message,
                    MessageTypeEnum.SHARE.value,
                    idToShareTo
                )
            }
        }

    }
}