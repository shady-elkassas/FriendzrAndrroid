package com.friendzrandroid.home.fragment.home.UserProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.UserProfileFragmentBinding
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.ReportStates
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.utils.ImageDialog
import com.friendzrandroid.utils.MenuUtil
import com.friendzrandroid.utils.ProfileUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FeedRequestUserProfileFragment : BaseFragment() {


    private lateinit var userName: String
    private var profileStatusKey: Int = -2
    val args: FeedRequestUserProfileFragmentArgs by navArgs()

    val userID by lazy { args.userID }//"4a73f195-9890-4c63-8334-f7f0142c61f1" }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        UserProfileFragmentBinding.inflate(layoutInflater)
    }

    private val viewModelFeedRequest: FeedRequestUserProfileViewModel by viewModels()

    private lateinit var userImage: String

    override val baseViewModel: BaseViewModel
        get() = viewModelFeedRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        setClicks()

        binding.profileSwipeToRefresh.changeColor()

        viewModelFeedRequest.getUserProfile(userID)

        viewModelFeedRequest.updateUserState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    //showLoading()
                }

                is DataState.NewData -> {
                    //hideLoading()
                    if (it.data) viewModelFeedRequest.getUserProfile(userID)
                }

                is DataState.FailData -> {
                    //hideLoading()
                    showToast(it.message)
                }
                else -> {}
            }
        }
        viewModelFeedRequest.userProfile.observe(viewLifecycleOwner) {
            binding.profileSwipeToRefresh.isRefreshing = false
            when (it) {
                is DataState.Loading -> {
                    ProfileUtil.showLoading(binding, true)
                }
                is DataState.NewData -> {
                    setUserData(it.data)
                    ProfileUtil.showLoading(binding, false)
                }

                is DataState.FailData -> {
                    ProfileUtil.showLoading(binding, false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
        trackScreenName("User profile Screen")
        return binding.root
    }

    private fun setUserData(data: UserProfileData) {
        profileStatusKey = data.key
        userName = data.userName
        binding.tvInboxTitle.text = data.userName

        userImage = data.userImage

        binding.userProfileImage.loadImage(data.userImage)
        binding.profileUserName.text = data.userName
        binding.profileSystemUserName.text = "@${data.displayedUserName}"
        binding.userAgeValue.text = data.age.toString()
        binding.userGenderValue.text =
            if (data.gender.equals("othergender")) "Other" else data.gender
        binding.profileUserBio.text = data.bio



        context?.let {
            ProfileUtil.addChipsViews(it, binding.profileUserEnjoyTags, data.listoftagsmodel, false)

            ProfileUtil.addChipsViews(it, binding.profileUserIamTags, data.iamList, false)
            ProfileUtil.addChipsViews(it, binding.profileUserPreferTags, data.prefertoList, false)
        }


        binding.BtnSendRequest.hide()
        binding.BtnsCurrentFriend.hide()
        binding.BtnsRequestActions.hide()


        when (data.key) {
            FeedKeyStatus.YOU_BLOCK_USER.key -> {
                binding.BtnSendRequest.show()
                binding.BtnSendRequest.tag = RequestKeyStatus.UN_BLOCK.key
                //binding.BtnSendRequest.background = ContextCompat.getDrawable(requireContext(),R.drawable.shape_round_blue)

                binding.BtnSendRequest.text = getText(R.string.unblok)
            }
            FeedKeyStatus.NORMAL_FEED_STATE.key -> {
                binding.BtnSendRequest.show()
                binding.BtnSendRequest.tag = RequestKeyStatus.SEND.key
                //binding.BtnSendRequest.background = ContextCompat.getDrawable(requireContext(),R.drawable.shape_round_blue)

                binding.BtnSendRequest.text = getText(R.string.sendRequest)
            }
            FeedKeyStatus.YOU_SENT_REQUEST.key -> {
                binding.BtnSendRequest.show()
                binding.BtnSendRequest.tag = RequestKeyStatus.CANCEL_REJECT.key
                binding.BtnSendRequest.text = getText(R.string.cancel_request)
                //binding.BtnSendRequest.background = ContextCompat.getDrawable(requireContext(),R.drawable.shape_round_gray)

            }
            FeedKeyStatus.IS_FRIEND.key -> {
                binding.BtnsCurrentFriend.show()
            }
            FeedKeyStatus.OTHER_USER_SEND_REQUEST.key -> {
                binding.BtnsRequestActions.show()
            }

        }


    }

    fun setClicks() {

        binding.userProfileImage.setOnClickListener {
            ImageDialog.setImageBigger(requireActivity(), userImage)
        }

        binding.profileSwipeToRefresh.setOnRefreshListener {
            viewModelFeedRequest.getUserProfile(userID)
        }

        binding.BtnSendRequest.apply {
            setOnClickListener {
                viewModelFeedRequest.changeStatus(
                    userID,
                    binding.BtnSendRequest.tag as Int
                )

                if (binding.BtnSendRequest.tag as Int == RequestKeyStatus.SEND.key) showButtonLoading(
                    resources.getString(R.string.title_sending)
                )
                else if (binding.BtnSendRequest.tag as Int == RequestKeyStatus.CANCEL_REJECT.key) showButtonLoading(
                    resources.getString(R.string.title_canceling)
                )
                else if (binding.BtnSendRequest.tag as Int == RequestKeyStatus.UN_BLOCK.key) showButtonLoading(
                    resources.getString(R.string.title_un_blocking)
                )
            }
        }


//        binding.BtnSendRequest.setOnClickListener {
//            viewModelFeedRequest.changeStatus(userID, binding.BtnSendRequest.tag as Int)
//        }


        binding.btnUserProfileUnfriend.apply {
            setOnClickListener {
                viewModelFeedRequest.changeStatus(
                    userID,
                    RequestKeyStatus.UNFRIEND.key
                )
                showButtonLoading(resources.getString(R.string.title_loading))
            }
        }
//        binding.btnUserProfileUnfriend.setOnClickListener {
//            viewModelFeedRequest.changeStatus(userID, RequestKeyStatus.UNFRIEND.key)
//        }

        binding.btnUserProfileBlock.apply {
            setOnClickListener {
                viewModelFeedRequest.changeStatus(userID, RequestKeyStatus.BLOCK.key)
                showButtonLoading(resources.getString(R.string.title_blocking))
            }
        }

        binding.btnUserProfileMessage.apply {
            setOnClickListener {

                val action =
                    FeedRequestUserProfileFragmentDirections.actionUserProfileFragmentToNavigationChat(

                        chatID = userID,
                        chatImage = userImage,
                        chatName = userName,
                        chatIsEvent = false,
                        myEvent = false,
                        isFriend = true
                    )
                findNavController().navigate(action)

            }
        }
//        binding.btnUserProfileBlock.setOnClickListener {
//            viewModelFeedRequest.changeStatus(userID, RequestKeyStatus.BLOCK.key)
//        }


        binding.btnAcceptRequest.apply {
            setOnClickListener {
                viewModelFeedRequest.changeStatus(userID, RequestKeyStatus.ACCEPT.key)
                showButtonLoading(resources.getString(R.string.title_accepting))
            }
        }
//        binding.btnAcceptRequest.setOnClickListener {
//            viewModelFeedRequest.changeStatus(userID, RequestKeyStatus.ACCEPT.key)
//        }


        binding.btnDeclineRequest.apply {
            setOnClickListener {
                viewModelFeedRequest.changeStatus(
                    userID,
                    RequestKeyStatus.CANCEL_REJECT.key
                )
                showButtonLoading(resources.getString(R.string.title_canceling))
            }
        }
//        binding.btnDeclineRequest.setOnClickListener {
//            viewModelFeedRequest.changeStatus(userID, RequestKeyStatus.CANCEL_REJECT.key)
//        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.imgProfileMenu.visibility = View.VISIBLE


        binding.imgProfileMenu.setOnClickListener {


            when (profileStatusKey) {
                FeedKeyStatus.IS_FRIEND.key -> {
                    showUserReportMenu(it, userID,false)


                }
                else -> {
                    showUserReportMenu(it, userID, true)

                    /*MenuUtil(
                        requireActivity(), it, R.menu.user_report_menu
                    ).showMenu { selectedMenuId ->
                        when (selectedMenuId) {
                            R.id.report -> {
                                val action =
                                    FeedRequestUserProfileFragmentDirections.actionUserProfileFragmentToReportFragment(
                                        userID, ReportStates.REPORT_USER.value
                                    )
                                findNavController().navigate(action)
                                return@showMenu true
                            }
                            else -> return@showMenu true
                        }
                    }*/

                }


            }



        }


    }

    private fun showUserReportMenu(view: View, userId: String, isNotFriend: Boolean) {
        MenuUtil(
            requireActivity(), view, R.menu.profile_user_option_menu
        ).showMenu { selectedMenuItem ->
            when (selectedMenuItem) {
                R.id.report -> {
                    val action =
                        FeedRequestUserProfileFragmentDirections.actionUserProfileFragmentToReportFragment(
                            userID, ReportStates.REPORT_USER.value
                        )
                    findNavController().navigate(action)
                    return@showMenu true
                }

                R.id.block -> {
                    ConfirmationDialog(
                        requireContext(), getString(R.string.title_block_dialog), true
                    ).showDialog {

                        if (it == 1) viewModelFeedRequest.changeStatus(
                            userID,
                            RequestKeyStatus.BLOCK.key,isNotFriend
                        )

                        findNavController().popBackStack()

                    }
                }
            }
            return@showMenu true
        }
    }

}