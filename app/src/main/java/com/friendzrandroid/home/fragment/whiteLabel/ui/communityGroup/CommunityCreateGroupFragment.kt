package com.friendzrandroid.home.fragment.whiteLabel.ui.communityGroup

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.CreateInboxGroupFragmentBinding
import com.friendzrandroid.databinding.FragmentCommunityCreateGroupBinding
import com.friendzrandroid.home.adapter.MyFriendsGroupAdapter
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import com.friendzrandroid.home.fragment.home.messages.group.create.CreateGroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CommunityCreateGroupFragment : BaseFragment(), FeedRequestAdapterListener<FeedRequestUserData> {

    private val TAG = "CreateGroupFragment"

    private val viewModel: CreateGroupViewModel by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentCommunityCreateGroupBinding.inflate(layoutInflater)
    }

    private val selectedGroupMember: ArrayList<String> = arrayListOf()

    override val baseViewModel: BaseViewModel
        get() = viewModel

    private lateinit var recAdapter: MyFriendsGroupAdapter

    var file: File? = null
    lateinit var imageUtil: SelectImageUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.totalItemCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.groupMemberRecycler.show()
            } else {
                binding.groupMemberRecycler.hide()
            }
        }

        setObservers()
        setClicks()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleSearch()
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

        recAdapter = MyFriendsGroupAdapter(viewModel, this, isGroupChatFragment = true)


        binding.groupMemberRecycler.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recAdapter.showLoading()

    }

    private fun setObservers() {

        val obs = MutableLiveData<Uri>()
        imageUtil = SelectImageUtil(this, obs)

        obs.observe(viewLifecycleOwner) {
            file = File(it.path)
            binding.imageGroup.loadImage(it)
        }


        viewModel.isGroupCreated.observe(viewLifecycleOwner) {

            when (it) {
                is DataState.NewData -> {
                    binding.createGroupButtonDone.hideButtonLoading(
                        getString(R.string.title_created),
                        requireActivity()
                    ) {
                        findNavController().popBackStack()
                    }
                }
                is DataState.FailData -> {
                    binding.createGroupButtonDone.showButtonLoading(resources.getString(R.string.title_done))
                    showToast(it.message)
                }
                else -> {}
            }

        }
    }

    private fun setClicks() {
        binding.imageCreateGroupContainer.setOnClickListener {
            imageUtil.selectImage(false)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.createGroupButtonDone.setOnClickListener {
            createGroupChat()
        }
    }


    private fun handleSearch() {

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

    private fun createGroupChat() {

        val groupName = binding.groupNameEdit.text.toString()
        if (groupName.isEmpty()) {
            showToast("Please enter group name")
            return
        }

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val groupCreationDate = "$currentDate $currentTime"

        val groupListOfIds = selectedGroupMember.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ",",
            transform = { "\"$it\"" }
        )

        Log.e(TAG, "createGroupChat: $selectedGroupMember")
        Log.e(TAG, "createGroupChat: $groupListOfIds")
        if (selectedGroupMember.isNullOrEmpty()) {
            showToast("Please select group members")
            return
        }

        if (file != null) {
            binding.createGroupButtonDone.showButtonLoading(resources.getString(R.string.title_creating))
            viewModel.createGroupChat(groupName, groupCreationDate, groupListOfIds, file)
        } else {
            showToast("Select group image.")
            return
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