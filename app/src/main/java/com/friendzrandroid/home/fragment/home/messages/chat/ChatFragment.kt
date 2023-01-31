package com.friendzrandroid.home.fragment.home.messages.chat

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.*

import android.net.Uri

import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.BaseAdapterListener
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.FragmentChatBinding
import com.friendzrandroid.home.adapter.EventChatAdapter
import com.friendzrandroid.home.data.MessageAttachedVM
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.data.model.MessageData
import com.friendzrandroid.home.data.model.enum.MessageTypeEnum
import com.friendzrandroid.home.data.model.enum.ReportStates
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.fragment.home.UserProfile.FeedRequestUserProfileFragmentDirections
import com.friendzrandroid.utils.FileUtils
import com.friendzrandroid.utils.MenuUtil
import com.google.firebase.messaging.RemoteMessage

import dagger.hilt.android.AndroidEntryPoint

import java.io.File
import java.util.*


//@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChatFragment : BaseFragment(), BaseAdapterListener<EventItemData> {
    private lateinit var receiver: BroadcastReceiver
    var TAG: String = "ChatFragment"

    companion object {
        //const val MESSAGE_BROAD_CAST_ACTION = "MESSAGE_BROAD_CAST_ACTION"
        var receiverIsActive = false
        var receiverChatId: String = ""
    }

    val args: ChatFragmentArgs by navArgs()

    val chatID by lazy {
        args.chatID
    }
    val isFreind by lazy {
        args.isFriend
    }

    val chatImage by lazy {

        args.chatImage
    }

    val isChatAdmin by lazy {
        args.isAdminGroup
    }
  val isWhiteLabel by lazy {
        args.isAdminGroup
    }

    val chatName by lazy {

        args.chatName
    }

    val chatIsEvent by lazy {

        args.chatIsEvent
    }

    val leftChat by lazy {
        args.leftChat
    }

    val leftGroup by lazy {
        args.leftGroup
    }

    val chatIsGroup by lazy {
        args.chatIsGroup
    }

    val myEvent by lazy {
        args.myEvent
    }

    lateinit var recAdapter: EventChatAdapter
    val obs = MutableLiveData<Uri>()
    val obsFile = MutableLiveData<String>()
    var isFile: Boolean = false
    private val viewModel: ChatViewModel by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChatBinding.inflate(layoutInflater)
    }
    override val baseViewModel: BaseViewModel
        get() = viewModel

    lateinit var imageUtil: SelectImageUtil
    var REQUEST_CODE_STORAGE_ACCESS: Int = 201
    var RECORD_REQUEST_CODE: Int = 202

    val REQUEST_EXTERNAL_PERMISSION_CODE = 666

    val PERMISSIONS_EXTERNAL_STORAGE =
        arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )


    //Declare PickiT
    private val BUFFER_SIZE = 1024 * 2
    private val IMAGE_DIRECTORY = "/demonuts_upload_gallery"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        broadCastReceiver()




        chatImage?.let { binding.chatImage.loadImage(it) }
        binding.chatTitle.text = chatName
        viewModel.chatID = chatID
        viewModel.isEvent = chatIsEvent
        viewModel.isFriend = isFreind
        viewModel.isChatGroup = chatIsGroup

        imageUtil = SelectImageUtil(this, obs)



        totalItemCountObserver()

        updateUserStateObserver()

        closeChatValidation()






        lifecycleObserver(obs, obsFile)

        checkIfNotUserChat()

        //context, listener, activity
        //context, listener, activity


        initClicks()
        return binding.root
    }

    private fun totalItemCountObserver() {
        viewModel.totalItemCount.observe(viewLifecycleOwner) {
            val x = viewModel.pageNumber
            if (it == 0 && viewModel.pageNumber == 1) {
                //recAdapter.adapterList.clear()
                binding.messageRec.hide()
                //binding.notFriendContainer.show()
            } else {
                binding.messageRec.show()
            }
        }

    }


    private fun sendMessageListener() {

        binding.sendNewMessage.setOnClickListener {
            val message = binding.edtMessage.text.toString()
            if (message.isEmpty()) {
                return@setOnClickListener
            } else {
                binding.edtMessage.setText("")


                val currentDate = Date()
                val mesgDate = currentDate.FormatToDate()
                val mesgTime = currentDate.FormatToTime()

                val data = MessageData(
                    currentuserMessage = true,
                    messages = message,
                    messagetype = MessageTypeEnum.MESSAGE.value,
                    messagesdate = "Today",
                    messagestime = mesgTime,
                    userimage = UserSessionManagement.getUserData().userImage,
                    messageAttachedVM = listOf(),
                    userId = "",
                    username = "",
                    messagestype = null,
                    userMessagessId = "",
                    id = "",
                    myevent = myEvent,
                    eventChatID = if (chatIsEvent) chatID else "",
                    eventData = null
                )

                recAdapter.append(data)
                binding.messageRec.scrollToPosition(0)


                if (!binding.messageRec.isVisible)
                    binding.messageRec.show()

//                if (!binding.messageRec.isVisible) {
//                    recAdapter.adapterList.clear()
//                    recAdapter.append(data)
//                    binding.messageRec.show()
//                } else
//                    recAdapter.append(data)

                viewModel.sendMessage(message, MessageTypeEnum.MESSAGE, null)
                Log.d("chat", "sendMessageListener:  viewModel.sendMessage")
            }
        }

    }

    private fun checkIfNotUserChat() {
//        if (!chatIsGroup) binding.btnMenu.hide()


        binding.btnMenu.setOnClickListener {

            if (chatIsGroup) {
                if (isChatAdmin) {
                    val action =
                        ChatFragmentDirections.actionNavigationChatToDetailsGroupChatFragment(
                            chatID,
                            isChatAdmin
                        )
                    findNavController().navigate(action)
                } else {
                    showUserReportMenuForGroup(binding.btnMenu, chatID)
                }
            } else if (chatIsEvent) {

                if (isChatAdmin) {
                    val action =
                        ChatFragmentDirections.actionNavigationChatToDetailsGroupChatFragment(
                            chatID,
                            isChatAdmin
                        )
                    findNavController().navigate(action)
                } else {

                    showUserReportMenuForGroup(binding.btnMenu, chatID)
                }
            }
        }


        if (isFreind) binding.btnMenu.apply {
            setOnClickListener {
                showUserReportMenu(binding.btnMenu, chatID)

            }
            show()
        }


         if (chatIsGroup) {

            binding.btnMenu.show()
        } else if (chatIsEvent) {
             binding.btnMenu.show()

         }else if (isFreind) {
             binding.btnMenu.show()

         } else {
            binding.btnMenu.hide()

        }


    }

    private fun lifecycleObserver(obs: MutableLiveData<Uri>, obsFile: MutableLiveData<String>) {
        obs.observe(viewLifecycleOwner) {
//            file = File(it.path)
//            binding.imgUserProfile.loadImage(it)

            isFromImagePicker = true

            val currentDate = Date()
            val mesgDate = currentDate.FormatToDate()
            val mesgTime = currentDate.FormatToTime()

            val data = MessageData(
                currentuserMessage = true,
                messages = "Image",
                messagetype = MessageTypeEnum.IMAGE.value,
                messagesdate = "Today",
                messagestime = mesgTime,
                userimage = UserSessionManagement.getUserData().userImage,
                messageAttachedVM = listOf(MessageAttachedVM(it.toString())),
                userId = "",
                username = "",
                messagestype = null,
                userMessagessId = "",
                id = "",
                myevent = myEvent,
                eventChatID = if (chatIsEvent) chatID else "",
                eventData = null
            )

            //Log.e(TAG, "onCreateView: $data")

            recAdapter.append(data)
            binding.messageRec.scrollToPosition(0)


            if (isFile) {
                viewModel.sendMessage("", MessageTypeEnum.FILE, it.path?.let { it1 -> File(it1) })
                Log.d(TAG, "obs:  is file")

            } else {
                viewModel.sendMessage("", MessageTypeEnum.IMAGE, it.path?.let { it1 -> File(it1) })
                Log.d(TAG, "obs:  is image")

            }

            isFromImagePicker = false

        }

        obsFile.observe(viewLifecycleOwner) {
//            file = File(it.path)
//            binding.imgUserProfile.loadImage(it)

            isFromImagePicker = true

            val currentDate = Date()
            val mesgDate = currentDate.FormatToDate()
            val mesgTime = currentDate.FormatToTime()


            //Log.e(TAG, "onCreateView: $data")


            if (isFile) {

                val data = MessageData(
                    currentuserMessage = true,
                    messages = "Image",
                    messagetype = MessageTypeEnum.FILE.value,
                    messagesdate = "Today",
                    messagestime = mesgTime,
                    userimage = UserSessionManagement.getUserData().userImage,
                    messageAttachedVM = listOf(MessageAttachedVM(it.toString())),
                    userId = "",
                    username = "",
                    messagestype = null,
                    userMessagessId = "",
                    id = "",
                    myevent = myEvent,
                    eventChatID = if (chatIsEvent) chatID else "",
                    eventData = null
                )


                viewModel.sendMessage("", MessageTypeEnum.FILE, it.let { it1 -> File(it1) })
                Log.d(TAG, "obsFile:  is file")
                recAdapter.append(data)
                binding.messageRec.scrollToPosition(0)

            } else {

                val data = MessageData(
                    currentuserMessage = true,
                    messages = "Image",
                    messagetype = MessageTypeEnum.IMAGE.value,
                    messagesdate = "Today",
                    messagestime = mesgTime,
                    userimage = UserSessionManagement.getUserData().userImage,
                    messageAttachedVM = listOf(MessageAttachedVM(it.toString())),
                    userId = "",
                    username = "",
                    messagestype = null,
                    userMessagessId = "",
                    id = "",
                    myevent = myEvent,
                    eventChatID = if (chatIsEvent) chatID else "",
                    eventData = null
                )
                viewModel.sendMessage("", MessageTypeEnum.IMAGE, it.let { it1 -> File(it1) })
                Log.d(TAG, "obsFile:  is image")
                recAdapter.append(data)
                binding.messageRec.scrollToPosition(0)
            }

            isFromImagePicker = false

        }


    }

    private fun closeChatValidation() {

        Log.e(TAG, "onCreateView: Event: $chatIsEvent, Friend: $isFreind, Group: $chatIsGroup")

        if (!isFreind && !chatIsEvent && !chatIsGroup) {
            showNonFriend(getString(R.string.you_are_not_friend_with_this_account))
        } else if (!isFreind && chatIsEvent && leftChat >= 1) {
            showNonFriend(getString(R.string.you_have_left_event))
        } else if (!isFreind && chatIsGroup && leftGroup >= 1)
            showNonFriend(getString(R.string.you_have_left_group))
        else {
            binding.sendMessageContainer.show()
            binding.notFriendContainer.hide()
        }
    }

    private fun updateUserStateObserver() {

        viewModel.updateUserState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.NewData -> {
                    findNavController().popBackStack()
                }

                is DataState.FailData -> showToast(state.message)
                else -> {}
            }
        }

    }

    private fun showNonFriend(text: String) {
        binding.sendMessageContainer.hide()
        binding.notFriendContainer.show()

        binding.notFriendMessage.text = text
    }

    private fun showUserReportMenu(view: View, userId: String) {
        MenuUtil(
            requireActivity(),
            view,
            R.menu.user_option_menu
        ).showMenu { selectedMenuItem ->
            when (selectedMenuItem) {
                R.id.report -> {
                    val action = ChatFragmentDirections.actionNavigationChatToReportFragment(
                        userId = userId,
                        reportType = ReportStates.REPORT_USER.value
                    )
                    findNavController().navigate(action)
                    return@showMenu true
                }

                R.id.unfriend -> {
                    ConfirmationDialog(
                        requireContext(),
                        getString(R.string.title_unfriend_dialog),
                        true
                    ).showDialog {
                        if (it == 1)
                            viewModel.changeUserState(chatID, RequestKeyStatus.UNFRIEND.key)
                    }
                }

                R.id.block -> {
                    ConfirmationDialog(
                        requireContext(),
                        getString(R.string.title_block_dialog),
                        true
                    ).showDialog {
                        if (it == 1)
                            viewModel.changeUserState(chatID, RequestKeyStatus.BLOCK.key)
                    }
                }
            }
            return@showMenu true
        }
    }

    private fun showUserReportMenuForGroup(view: View, userId: String) {
        MenuUtil(
            requireActivity(), view, R.menu.chat_group_report_menu
        ).showMenu { selectedMenuItem ->
            when (selectedMenuItem) {
                R.id.report -> {
                    val action =
                        ChatFragmentDirections.actionNavigationChatToReportFragment(
                            chatID,
                            reportType = ReportStates.REPORT_GROUP.value
                        )
                    findNavController().navigate(action)
                    return@showMenu true
                }

                R.id.details -> {

                    if (chatIsEvent) {
                        val action =
                            ChatFragmentDirections.actionNavigationChatToEventDetailsFragment(
                                chatID
                            )
                        findNavController().navigate(action)

                    } else {
                        val action =
                            ChatFragmentDirections.actionNavigationChatToDetailsGroupChatFragment(
                                chatID,
                                isChatAdmin
                            )
                        findNavController().navigate(action)

                    }

                }


            }
            return@showMenu true
        }
    }

    private fun initRecyclerView() {

        recAdapter = EventChatAdapter(viewModel, requireActivity(), this)
//        val bottomItemDecoration = ItemMarginBottomDecoration()
//        binding.messageRec.addItemDecoration(bottomItemDecoration)

        val lManager = LinearLayoutManager(requireContext())
        lManager.reverseLayout = true
        lManager.stackFromEnd = true
        binding.messageRec.apply {
            adapter = recAdapter
            layoutManager = lManager
            addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->

                if (bottom < oldBottom)
                    this.postDelayed(Runnable {
                        this.smoothScrollToPosition(0)
                    }, 100)
            }
        }
        recAdapter.showLoading()


    }

    private fun initClicks() {
        sendMessageListener()
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }



            binding.chatImage.setOnClickListener {

                if (isFreind && !isWhiteLabel) {

                    val action =
                        ChatFragmentDirections.actionNavigationChatToUserProfileFragment(userID = chatID)
                    findNavController().navigate(action)
                }


            }



        binding.btnAttach.setOnClickListener {

            isFile = false
            imageUtil.selectImage(true)


        }


    }


    private val getResults =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {


                val value = obsFile.value
                //                Log.e(TAG, "file Path is : $path")
                Log.e(TAG, " obsFile.value  is : $value")


                var fileUri = FileUtils.getPath(context, result.data?.data)

//                val fileUri = FileUtliKotlin().getPath(requireContext(), result.data?.data!!)


                obsFile.value = fileUri

//                pickiT!!.getPath(result.data!!.getData(), Build.VERSION.SDK_INT)

                isFile = true


            }
        }


    private var isFromImagePicker: Boolean = false
    override fun onResume() {
        receiverIsActive = true
        receiverChatId = chatID

        if (!this::recAdapter.isInitialized) {
            initRecyclerView()
        } else if (!this::recAdapter.isInitialized && !isFromImagePicker)
            recAdapter.reloadData()

        //viewModel.reload()

        super.onResume()
    }

    override fun onPause() {
        receiverIsActive = false

        super.onPause()
    }

    override fun onDestroy() {
        receiverIsActive = false
        //requireActivity().unregisterReceiver(receiver)

        super.onDestroy()
    }


    private fun broadCastReceiver() {

        receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

                intent.extras?.let {

                    val message: RemoteMessage = it.getParcelable("newMessage")!!
//                    message.notification!!.body
//
                    Log.e(TAG, "onReceive: $message")

                    if (receiverIsActive) {
                        // pass new message data in append function
                        var eventData: EventItemData? = null
                        if (message.data["Messagetype"]!!.toInt() == MessageTypeEnum.SHARE.value) {
                            eventData = EventItemData(
                                image = message.data["messsageLinkEvenImage"]!!,
                                id = message.data["messsageLinkEvenId"]!!,
                                categorie = message.data["messsageLinkEvencategorie"]!!,
                                title = message.data["messsageLinkEvenTitle"]!!,
                                totalnumbert = message.data["messsageLinkEventotalnumbert"]!!.toInt(),
                                joined = message.data["messsageLinkEvenjoined"]!!.toInt(),
                                eventdate = message.data["messsageLinkEveneventdateto"]!!
                            )
                        }


                        val messageData = MessageData(
                            currentuserMessage = false,
                            messages = message.data["Body"]!!,
                            messagetype = message.data["Messagetype"]!!.toInt(),
                            messagesdate = message.data["date"]!!,
                            messagestime = message.data["time"]!!,
                            userimage = message.data["senderImage"]!!,
                            messageAttachedVM = listOf(),
                            userId = message.data["senderId"]!!,
                            username = message.data["senderDisplayName"]!!,
                            messagestype = message.data["Messagetype"],
                            userMessagessId = "",
                            id = "",
                            myevent = myEvent,
                            eventChatID = if (chatIsEvent) chatID else "",
                            eventData = eventData
                        )


                        Log.e(TAG, "onReceive: Message Data To Append: $messageData")

                        recAdapter.append(messageData)
                        binding.messageRec.scrollToPosition(0)


                    } else {

                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiver, IntentFilter("local-message"))

        //requireActivity().registerReceiver(receiver, IntentFilter(MESSAGE_BROAD_CAST_ACTION))

    }

    override fun onItemSelected(data: EventItemData) {
        val action = data.id.let {
            ChatFragmentDirections.actionNavigationChatToEventDetailsFragment(
                it
            )
        }
        if (action != null) {
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }


}