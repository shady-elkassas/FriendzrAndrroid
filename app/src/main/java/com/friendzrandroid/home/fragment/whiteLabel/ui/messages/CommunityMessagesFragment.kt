package com.friendzrandroid.home.fragment.whiteLabel.ui.messages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.paggingList.BaseAdapterListener
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.FragmentCommunityMessagesBinding

import com.friendzrandroid.home.adapter.InboxAdapter
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.InboxData
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.fragment.home.messages.inbox.InboxFragmentDirections
import com.friendzrandroid.home.fragment.home.messages.inbox.InboxViewModel
import com.friendzrandroid.home.fragment.more.settings.SettingsViewModel
import com.friendzrandroid.home.fragment.whiteLabel.CommunityActivity
import com.friendzrandroid.utils.AdsBannerUtil
import com.friendzrandroid.utils.ItemMarginBottomDecoration
import com.friendzrandroid.utils.SwipeHelper
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class CommunityMessagesFragment : BaseFragment(), BaseAdapterListener<InboxData> {

    private lateinit var receiver: BroadcastReceiver
    private lateinit var itemTouchHelper: ItemTouchHelper

    companion object {
        var isUserInInbox = false
    }

    private val viewModel: InboxViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentCommunityMessagesBinding.inflate(layoutInflater)
    }
    override val baseViewModel: BaseViewModel
        get() = viewModel

    private lateinit var recAdapter: InboxAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding.swipeToRefresh.changeColor()

        AdsBannerUtil.loadAds(binding.bannerAdView)

        broadCastReceiver()


        //handleSearch()
        handleSwip()




        goToCommunityPeople()

        recyclerObserver()
        trackScreenName("Inbox Screen")

        logout()





        return binding.root
    }

    private fun goToCommunityPeople() {
        binding.imgInboxEdit.setOnClickListener {
            val action =
                CommunityMessagesFragmentDirections.actionNavigationInboxToMyFriendsFragment()
            findNavController().navigate(action)

        }


    }

    private fun recyclerObserver() {
        viewModel.isEmptyList.observe(viewLifecycleOwner) {
            binding.swipeToRefresh.isRefreshing = false

            if (it) {
                binding.recChats.hide()
                binding.noDataContainer.show()
            } else {
                binding.recChats.show()
                binding.noDataContainer.hide()
//                if (!UserSessionManagement.isWhiteLabel()) {
//                    updateInboxBadge(UserSessionManagement.getInboxNumber())
//
//                }
            }
        }

    }

    private fun logout() {

        binding.userLogout.setOnClickListener {

//            val builder: AlertDialog.Builder? = activity?.let {
//                AlertDialog.Builder(it)
//            }
//
//            builder?.setTitle(getString(R.string.caution))
//
//            builder?.setMessage(getString(R.string.signOutmessage))
//
//            builder?.setIcon(R.drawable.ic_caution_sign)
//
//            builder?.setPositiveButton(
//                getString(R.string.yes)
//            ) { dialog, which -> setLogOutCall() }
//
//            builder?.setNegativeButton(
//                getString(R.string.no)
//            ) { dialog, which -> dialog.cancel() }
//
//            builder?.show()

            ConfirmationDialog(
                requireContext(),
                getString(R.string.signOutmessage),
                true
            ).showDialog {
                if (it == 1) {

                    setLogOutCall()

                }
            }
        }


    }

    private fun setLogOutCall() {
        lifecycleScope.launchWhenCreated {
            showLoading()
            val resposne = viewModel.validateResponse(settingsViewModel.logoutUseCase.execute())
            resposne?.let {
                if (it.isSuccessful) {

                    FirebaseMessaging.getInstance().deleteToken()
                    hideLoading()
                    UserSessionManagement.removeUserSession()

                    delay(100)
                    startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    })


                    (activity as CommunityActivity).socialMediaLogin.signOut()

                }
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleSearch()

        viewModel.chatOptionState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
//                    showToast(it.data)
//                    recAdapter.reloadData()
                }

                is DataState.FailData -> {
//                    showToast(it.message)
                }
                else -> {}
            }
        }
    }

    fun handleSwip() {
        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.reload()
        }
    }

    override fun onRefresh() {
        super.onRefresh()
        viewModel.reload()
    }

    override fun onResume() {
        super.onResume()
        isUserInInbox = true

//        if (!UserSessionManagement.isWhiteLabel()) {
//            updateInboxBadge(UserSessionManagement.getInboxNumber())
//
//        }

        Log.e("Inbox", "onResume: ${this::recAdapter.isInitialized}")

        viewModel.pageNumber = 1
        if (!this::recAdapter.isInitialized) {
            initRecyclerView()
        } else
            recAdapter.reloadData()
    }

    override fun onPause() {
        super.onPause()
        isUserInInbox = false
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
                    //viewModel.search(txt)
                    //recAdapter.reloadData()
                } else {
                    recAdapter.reloadData()
                }
            }

            return@setOnEditorActionListener false
        }
    }

    private fun initRecyclerView() {

        //viewModel.pageNumber = 1
        recAdapter = InboxAdapter(viewModel, this)

        val bottomItemDecoration = ItemMarginBottomDecoration()
        binding.recChats.addItemDecoration(bottomItemDecoration)

        binding.recChats.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recAdapter.showLoading()


        initSwipeHelper()


    }

    private fun initSwipeHelper() {

        itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.recChats) {


            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()

                val unMuteButton = unMuteButton(position)
                val muteButton = muteButton(position)
                val exitButton = exitButton(position)
                val clearButton = clearButton(position)
                val deleteButton = deleteButton(position)

                val itemInSwipedPosition = recAdapter.adapterList[position]

                itemInSwipedPosition.data?.let {
                    buttons = when {

                        it.isfrind && !it.muit -> listOf(muteButton, deleteButton).reversed()


                        it.isfrind && it.muit -> listOf(unMuteButton, deleteButton).reversed()

                        it.isevent && !it.muit -> listOf(
                            muteButton,
                            exitButton,
                            deleteButton
                        ).reversed()

                        it.isevent && it.muit -> listOf(
                            unMuteButton,
                            exitButton,
                            deleteButton
                        ).reversed()


                        it.isChatGroup && it.muit -> listOf(
                            unMuteButton,
                            exitButton,
                            clearButton
                        ).reversed()

                        it.isChatGroup && !it.muit -> listOf(
                            muteButton,
                            exitButton,
                            clearButton
                        ).reversed()

                        it.isCommunityGroup && it.muit -> listOf(
                            unMuteButton,

                            clearButton
                        ).reversed()

                        it.isCommunityGroup && !it.muit -> listOf(
                            muteButton,

                            clearButton
                        ).reversed()


                        else -> {
                            listOf(muteButton, exitButton, clearButton).reversed()
                        }
                    }
                }
                return buttons
            }


        })



        itemTouchHelper.attachToRecyclerView(binding.recChats)
    }

    override fun onItemSelected(data: InboxData) {

//        val newInboxNumber = (UserSessionManagement.getInboxNumber() - data.message_not_Read)
//        if (newInboxNumber >= 0) {
//            UserSessionManagement.updateInboxNumber(newInboxNumber)
//        } else {
//            UserSessionManagement.updateInboxNumber(0)
//        }

        val action = CommunityMessagesFragmentDirections.actionNavigationToChat(
            chatID = data.id,
            chatImage = data.image,
            chatName = data.name,
            chatIsEvent = data.isevent,
            isFriend = data.isfrind,
            chatIsGroup = data.isChatGroup,
            leftChat = data.leavevent,
            leftGroup = data.leaveGroup,
            isAdminGroup = data.isChatGroupAdmin,
            isCommunityGroup = data.isCommunityGroup
        )

        findNavController().navigate(action)


    }
    var selectedChatPosition: Int = 0
    private fun clearButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            "Clear",
            14.0f,
            com.friendzrandroid.R.color.delete,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    selectedChatPosition = position
//                    showToast("ClearButton $position")

                    if (recAdapter.adapterList[position].data?.isChatGroup == true) {

                        viewModel.clearGroupChat(
                            id = recAdapter.adapterList[position].data?.id!!
                        )
                    }


                }
            })
    }

    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            "Delete",
            14.0f,
            com.friendzrandroid.R.color.delete,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    //showToast("ClearButton $position")

                    ConfirmationDialog(
                        requireContext(),
                        "Are you sure you want to delete ${recAdapter.adapterList[position].data?.name} chat?",
                        true
                    ).showDialog {
                        if (it == 1)

                            if (recAdapter.adapterList[position].data?.isChatGroup == true) {

                                viewModel.removeGroupChat(recAdapter.adapterList[position].data?.id!!)
                            } else {
                                viewModel.deleteChat(
                                    recAdapter.adapterList[position].data?.id!!,
                                    recAdapter.adapterList[position].data?.isevent!!
                                )
                            }
                    }
                }
            })
    }

    private fun muteButton(position: Int): SwipeHelper.UnderlayButton {


        return SwipeHelper.UnderlayButton(
            requireContext(),
            "Mute",
            14.0f,
            com.friendzrandroid.R.color.primary_color,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    //showToast("MuteButton $position")
                    selectedChatPosition = position

                    if (recAdapter.adapterList[position].data?.isChatGroup == true) {

                        viewModel.muteGroupChat(
                            id = recAdapter.adapterList[position].data?.id!!,
                            mute = true
                        )


                    } else {
                        viewModel.muteChat(
                            id = recAdapter.adapterList[position].data?.id!!,
                            mute = true,
                            isEvent = recAdapter.adapterList[position].data?.isevent!!
                        )

                    }


                }
            })
    }

    private fun unMuteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            "UnMute",
            14.0f,
            com.friendzrandroid.R.color.primary_color,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {

                    //showToast("UnMuteButton $position")
                    selectedChatPosition = position

                    if (recAdapter.adapterList[position].data?.isChatGroup == true) {

                        viewModel.muteGroupChat(
                            id = recAdapter.adapterList[position].data?.id!!,
                            mute = false
                        )


                    } else {
                        viewModel.muteChat(
                            id = recAdapter.adapterList[position].data?.id!!,
                            mute = false,
                            isEvent = recAdapter.adapterList[position].data?.isevent!!
                        )
                        // Workaround to reset swiped out views


                    }


                }
            })
    }

    private fun exitButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            "Exit",
            14.0f,
            android.R.color.black,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    //showToast("ExitButton $position")
                    ConfirmationDialog(
                        requireContext(),
                        "Are you sure you want to leave ${recAdapter.adapterList[position].data?.name} chat?",
                        true
                    ).showDialog {
                        if (it == 1)
                            if (recAdapter.adapterList[position].data?.isChatGroup == true)
                                viewModel.leaveGroupChat(recAdapter.adapterList[position].data?.id!!)
                            else
                                viewModel.leaveEventChat(recAdapter.adapterList[position].data?.id!!)
                    }
                }
            })
    }
    private fun broadCastReceiver() {

        receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

                intent.extras?.let {
                    val reload = it.getBoolean("reload")
                    if (reload) {
                        viewModel.reload()
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiver, IntentFilter("reload-inbox"))

    }
    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }


}