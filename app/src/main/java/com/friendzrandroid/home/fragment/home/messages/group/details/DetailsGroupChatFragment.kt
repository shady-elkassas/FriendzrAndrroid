package com.friendzrandroid.home.fragment.home.messages.group.details

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.CreateInboxGroupFragmentBinding
import com.friendzrandroid.home.adapter.ChatGroupMemberAdapter
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.groupchat.GroupChatResponse
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.utils.ImageDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailsGroupChatFragment : BaseFragment(), ChatGroupMemberAdapter.GroupAttendanceCallBack {

    private val TAG = "DetailsGroupChatFragmen"

    private val viewModel: DetailsGroupChatViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel

    private val args: DetailsGroupChatFragmentArgs by navArgs()
    val chatId by lazy { args.chatId }
    val isGroupAdmin by lazy { args.isGroupAdmin }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        CreateInboxGroupFragmentBinding.inflate(layoutInflater)
    }

    var file: File? = null
    lateinit var imageUtil: SelectImageUtil

    private lateinit var recAdapter: ChatGroupMemberAdapter

    private var removePosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createGroupButtonDone.hide()
        binding.groupNameEdit.isEnabled = false
        binding.tvGroupAddUser.show()


        setObservers()
        setClicks()
        initRecycler()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getChatDetails(chatId)
    }

    private fun setObservers() {
        val obs = MutableLiveData<Uri>()
        imageUtil = SelectImageUtil(this, obs)

        obs.observe(viewLifecycleOwner) {
            file = File(it.path)
            binding.imageGroup.loadImage(it)

        }

        viewModel.chatDetailsData.observe(viewLifecycleOwner) {
            when (it) {


                is DataState.NewData -> setChatData(it.data)
                is DataState.FailData -> showToast(it.message)
                else -> {}
            }
        }

        viewModel.updateChatState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    binding.createGroupButtonDone.hideButtonLoading(
                        resources.getString(R.string.title_updated),
                        requireActivity()
                    ) {
                        findNavController().navigate(R.id.action_detailsGroupChatFragment_to_navigation_Inbox)
                    }

                    //showToast("Updated")
                }
                is DataState.FailData -> {
                    binding.createGroupButtonDone.showButtonLoading(
                        resources.getString(R.string.title_save),
                    )
                    showToast(it.message)
                }
                else -> {}
            }
        }


        viewModel.removeUserState.observe(viewLifecycleOwner)
        {
            when (it) {
                is DataState.NewData -> recAdapter.removeUser(removePosition)
                is DataState.FailData -> showToast(it.message)
                else -> {}
            }
        }
    }

    private fun setClicks() {

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvEditGroupTitle.setOnClickListener {
            binding.imgUserProfileCamera.show()

            binding.groupNameEdit.isEnabled = true

            binding.imageGroup.setOnClickListener {
                imageUtil.selectImage(false)
            }

            binding.imgUserProfileCamera.setOnClickListener {
                imageUtil.selectImage(false)
            }

            binding.createGroupButtonDone.apply {
                show()
                text = resources.getString(R.string.title_save)
                setOnClickListener {
                    viewModel.updateChat(
                        chatId,
                        binding.groupNameEdit.text.toString().trim(),
                        file
                    )
                    showButtonLoading(resources.getString(R.string.title_saving))
                }
            }
        }


        binding.tvGroupAddUser.setOnClickListener {
            val action =
                DetailsGroupChatFragmentDirections.actionDetailsGroupChatFragmentToAddUserGroupFragment(
                    chatId
                )
            findNavController().navigate(action)
        }
    }

    private fun setChatData(data: GroupChatResponse) {
        if (isGroupAdmin){
            binding.tvEditGroupTitle.show()

        }else{
            binding.tvEditGroupTitle.hide()

        }

        binding.tvInboxTitle.text = resources.getString(R.string.title_group_details)
        binding.imgUserProfileCamera.hide()

        binding.imageGroup.apply {
            loadImage(data.image)
            setOnClickListener {
                ImageDialog.setImageBigger(requireActivity(), data.image)
            }
        }

        binding.groupNameEdit.setText(data.name)
        recAdapter.setList(data.chatGroupSubscribers)


    }

    private fun initRecycler() {
        recAdapter = ChatGroupMemberAdapter(this, isGroupAdmin)
        binding.groupMemberRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recAdapter
        }
        recAdapter.showLoading()
    }

    override fun onActionTapper(
        btnView: View,
        userId: String,
        position: Int
    ) {

        val popupMenu = PopupMenu(requireActivity(), btnView)

        popupMenu.menuInflater.inflate(R.menu.user_event_actions, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { itemMenu ->

            when (itemMenu.itemId) {
                R.id.remove -> {

                    ConfirmationDialog(
                        requireContext(),
                        "Are you sure you want to remove ${recAdapter.mList[position].userName}",
                        true
                    ).showDialog {
                        if (it == 1) {
                            val currentDate =
                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                            val currentTime =
                                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            val dateOfKick = "$currentDate $currentTime"

                            removePosition = position
                            viewModel.removeUserFromChat(chatId, userId, dateOfKick)
                        }
                    }

                }
                R.id.block -> {
                    //viewModel.removeUserFromChat(groupMemberData.userID)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }
}