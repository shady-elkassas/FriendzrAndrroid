package com.friendzrandroid.home.fragment.home.messages.group.addUser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.hideButtonLoading
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.core.utils.showButtonLoading
import com.friendzrandroid.databinding.AllUsersFragmentBinding
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.adapter.MyFriendsAddAdapter
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import com.friendzrandroid.home.domain.model.UserWithGroupRequest
import com.friendzrandroid.home.fragment.home.events.addEvent.AddEventFragment
import com.friendzrandroid.home.fragment.home.events.editEvents.EditEventFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddUserGroupFragment : BaseFragment(), FeedRequestAdapterListener<FeedRequestUserData> {

    private val args: AddUserGroupFragmentArgs by navArgs()
    val chatId by lazy { args.chatId }
    val listOfSelectedUsersIds by lazy { args.listOfSelectedUsers }
    val isEditEvent by lazy { args.isEditEvent }

    private val viewModel: AddUserGroupViewModel by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        AllUsersFragmentBinding.inflate(layoutInflater)
    }

    private var selectedGroupMember: ArrayList<String> = arrayListOf()

    override val baseViewModel: BaseViewModel
        get() = viewModel

    private lateinit var recAdapter: MyFriendsAddAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        setClicks()
        setObservers()

    }

    private fun setObservers() {

        viewModel.totalItemCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.FeedRecycler.show()
            } else {
                binding.FeedRecycler.hide()
                binding.noDataContainer.show()
                binding.addUsersDoneButton.hide()
            }
        }

        viewModel.addUserToGroupState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    binding.addUsersDoneButton.hideButtonLoading("Added", requireActivity()) {
                        //showToast("Added new users")
                        findNavController().popBackStack()
                    }
                }
                is DataState.FailData -> {
                    binding.addUsersDoneButton.showButtonLoading(resources.getString(R.string.title_done))
                    showToast(it.message)
                }
                else -> {}
            }
        }
    }

    private fun setClicks() {
        binding.addUsersDoneButton.apply {
            setOnClickListener {
                addUsers()
                showButtonLoading(resources.getString(R.string.title_adding))
            }
        }
//        binding.addUsersDoneButton.setOnClickListener {
//            addUsers()
//        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!this::recAdapter.isInitialized) {
            initRecycler()
        } else {
            recAdapter.reloadData()
        }

        if (!listOfSelectedUsersIds.isNullOrEmpty())
            listOfSelectedUsersIds?.let {
                for (id in it)
                    selectedGroupMember.add(id)
            }
    }

    private fun initView() {
        binding.tvInboxTitle.text = "Select Friends"
        binding.tvInboxCreateConversation.hide()
        binding.addUsersDoneButton.show()
    }

    private fun initRecycler() {

        recAdapter = MyFriendsAddAdapter(
            viewModel,
            this,
            isGroupChatFragment = true,
            selectedUsersIds = listOfSelectedUsersIds,
            isEditEvent = isEditEvent
        )

        binding.FeedRecycler.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recAdapter.showLoading()
    }

    private fun addUsers() {

        if (chatId != null) {
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val groupCreationDate = "$currentDate $currentTime"

            val groupListOfIds = selectedGroupMember.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ",",
                transform = { "\"$it\"" }
            )

            val requestBody = UserWithGroupRequest(chatId!!, groupCreationDate, groupListOfIds)

            viewModel.addUserToGroup(requestBody)

        } else {

            Log.e("Add TAG", "addUsers: $selectedGroupMember")

            if (isEditEvent) {
                EditEventFragment.firstStart = false
                EditEventFragment.selectedAttendee = selectedGroupMember
            } else AddEventFragment.selectedAttendee = selectedGroupMember.toTypedArray()

            findNavController().popBackStack()
        }
    }


    override fun onItemSelected(data: FeedRequestUserData) {}
    override fun onActionRequest(
        newStatus: RequestKeyStatus,
        position: Int,
        data: FeedRequestUserData,
        afterSuccessStatus: FeedKeyStatus
    ) {
        //Log.e(TAG, "onActionRequest: InCreateGroup ${data.userId}, ${data.userName}")

        if (selectedGroupMember.contains(data.userId))
            selectedGroupMember.remove(data.userId)
        else
            selectedGroupMember.add(data.userId)

        //Log.e(TAG, "onActionRequest: SelectedMembers $selectedGroupMember")
    }

}