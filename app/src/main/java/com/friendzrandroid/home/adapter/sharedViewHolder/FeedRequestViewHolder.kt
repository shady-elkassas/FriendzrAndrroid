package com.friendzrandroid.home.adapter.sharedViewHolder

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.paggingList.BasePagingModel
import com.friendzrandroid.core.paggingList.BaseViewHolder
import com.friendzrandroid.core.paggingList.DataType
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.ItemFeedBinding
import com.friendzrandroid.databinding.ItemRequestBinding

import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus

open class FeedRequestViewHolder(
    private val myView: ViewDataBinding
) :
    BaseViewHolder<FeedRequestUserData>(myView) {

    override fun bind(pageData: BasePagingModel<FeedRequestUserData>) {
        when (pageData.type) {
            DataType.LOADING -> {
                when (myView) {
                    is com.friendzrandroid.databinding.ItemFeedBinding -> {
                        myView.shimmerLoading.startShimmer()
                        myView.btnSendRequest.hide()
                    }
                    is com.friendzrandroid.databinding.ItemRequestBinding -> {

                        myView.actionsContainer.hide()

                    }
                }
            }
            else -> {}
        }
    }

}

//..................................................................................................

class FeedViewHolder(val binding: ItemFeedBinding) :
    FeedRequestViewHolder(binding) {

    override fun bind(
        pageData: BasePagingModel<FeedRequestUserData>,
        position: Int,
        listener: FeedRequestAdapterListener<FeedRequestUserData>
    ) {
        when (pageData.type) {
            DataType.DATA -> {
                binding.shimmerLoading.stopShimmer()
                binding.shimmerLoading.hideShimmer()
                val data = pageData.data!!
                binding.cimgFeedUserImage.loadImage(data.image)
                binding.tvFeedItemTitle.text = data.userName
                binding.tvFeedUserName.text = "@${data.displayedUserName}"

                val token = UserSessionManagement.getKeyAuthToken()


                if (token != null && !token.equals("")) {
                    binding.tvFeedInterestsMatch.text =
                        "${data.interestMatchPercent}% interests match"

                    binding.feedInterestsMatchProgressBar.progress = data.interestMatchPercent
                } else {

                    binding.tvFeedInterestsMatch.hide()

                    binding.feedInterestsMatchProgressBar.hide()
                }
                if (data.key == FeedKeyStatus.NORMAL_FEED_STATE.key) {
                    binding.btnSendRequest.background =
                        ContextCompat.getDrawable(
                            binding.root.context,
                            R.drawable.shape_send_request
                        )
                    binding.btnSendRequest.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.white
                        )
                    )
                    binding.btnSendRequest.text = binding.root.context.getText(R.string.sendRequest)

                    binding.btnSendRequest.apply {

                        setOnClickListener {
                            if (token != null && !token.equals("")) {
                                listener.onActionRequest(
                                    RequestKeyStatus.SEND,
                                    position,
                                    data,
                                    FeedKeyStatus.YOU_SENT_REQUEST
                                )
                                showButtonLoading(context.getString(R.string.title_sending))

                            } else {

                                context.startActivity(
                                    Intent(
                                        context,
                                        AuthActivity::class.java
                                    ).apply {
                                        flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    })
                            }
                        }
                    }
//                    binding.btnSendRequest.setOnClickListener {
//                        listener.onActionRequest(
//                            RequestKeyStatus.SEND,
//                            position,
//                            data,
//                            FeedKeyStatus.YOU_SENT_REQUEST
//                        )
//                    }

                } else {
                    binding.btnSendRequest.background =
                        ContextCompat.getDrawable(
                            binding.root.context,
                            R.drawable.shape_request_sent
                        )
                    binding.btnSendRequest.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.primary_color
                        )
                    )
                    binding.btnSendRequest.text =
                        binding.root.context.getText(R.string.cancel_request)


                    binding.btnSendRequest.apply {
                        setOnClickListener {
                            listener.onActionRequest(
                                RequestKeyStatus.CANCEL_REJECT,
                                position,
                                data,
                                FeedKeyStatus.NORMAL_FEED_STATE
                            )
                            showButtonLoading(context.getString(R.string.title_canceling))
                        }
                    }
//                    binding.btnSendRequest.setOnClickListener {
//                        listener.onActionRequest(
//                            RequestKeyStatus.CANCEL_REJECT,
//                            position,
//                            data,
//                            FeedKeyStatus.NORMAL_FEED_STATE
//                        )
//                    }
                }


            }
            else -> {}
        }
    }

}

//..................................................................................................

class RequestViewHolder(
    val binding: ItemRequestBinding,
    private val isInRequests: Boolean = false
) :
    FeedRequestViewHolder(binding) {

    override fun bind(
        pageData: BasePagingModel<FeedRequestUserData>,
        position: Int,
        listener: FeedRequestAdapterListener<FeedRequestUserData>
    ) {
        when (pageData.type) {
            DataType.DATA -> {

                binding.shimmerLoading.stopShimmer()
                binding.shimmerLoading.hideShimmer()
                val data = pageData.data!!
                binding.userImage.loadImage(data.image)
                binding.displayUserNameTxt.text = data.userName
                binding.userNameTxt.text = "@${data.displayedUserName}"
                val token = UserSessionManagement.getKeyAuthToken()


                if (token != null && !token.equals("")) {
                    binding.tvFeedInterestsMatch.text =
                        "${data.interestMatchPercent}% interests match"

                    binding.feedInterestsMatchProgressBar.progress = data.interestMatchPercent
                } else {

                    binding.tvFeedInterestsMatch.hide()

                    binding.feedInterestsMatchProgressBar.hide()
                }


                if (isInRequests)
                    binding.requestDateTxt.text = data.regestdata

                binding.btnAcceptRequest.setOnClickListener {
                    listener.onActionRequest(
                        RequestKeyStatus.ACCEPT,
                        position,
                        data,
                        FeedKeyStatus.IS_FRIEND
                    )

                }
                binding.btnDeclineRequest.setOnClickListener {
                    listener.onActionRequest(
                        RequestKeyStatus.CANCEL_REJECT,
                        position,
                        data,
                        FeedKeyStatus.UPDATE_DECLINED_REQUEST
                    )

                }
            }
            else -> {}
        }
    }
}
//..................................................................................................


class RequestUpdateViewHolder(
    val binding: com.friendzrandroid.databinding.ItemRequestUpdateBinding,
    private val isInbox: Boolean = false,
    private val isGroupChat: Boolean = false,
    private val listOfSelectedUsers: Array<String>? = null,
    private val isEditEvent: Boolean = false
) :
    FeedRequestViewHolder(binding) {

    override fun bind(
        pageData: BasePagingModel<FeedRequestUserData>,
        position: Int,
        listener: FeedRequestAdapterListener<FeedRequestUserData>
    ) {
        when (pageData.type) {
            DataType.DATA -> {


                val data = pageData.data!!
                binding.userImage.loadImage(data.image)
                binding.displayUserNameTxt.text = data.userName
                binding.userNameTxt.text = "@${data.displayedUserName}"

                val token = UserSessionManagement.getKeyAuthToken()


                if (token != null && !token.equals("")) {
                    binding.tvFeedInterestsMatch.text =
                        "${data.interestMatchPercent}% interests match"

                    binding.feedInterestsMatchProgressBar.progress = data.interestMatchPercent
                } else {

                    binding.tvFeedInterestsMatch.hide()

                    binding.feedInterestsMatchProgressBar.hide()
                }

                if (data.key == FeedKeyStatus.IS_FRIEND.key && !isInbox && !isGroupChat) {

                    binding.btnMessage.show()
                    binding.txtRequestCanceled.hide()
                    binding.btnMessage.setOnClickListener { buttonAction(listener, data) }

                } else if (data.key == FeedKeyStatus.IS_FRIEND.key && isInbox && !isGroupChat) {

                    binding.btnMessage.hide()
                    binding.txtRequestCanceled.hide()
                    binding.friendDataContainer.setOnClickListener { buttonAction(listener, data) }

                } else if (data.key == FeedKeyStatus.IS_FRIEND.key && isGroupChat) {

                    binding.btnMessage.hide()
                    binding.txtRequestCanceled.hide()

                    binding.friendCheckBox.isChecked =
                        listOfSelectedUsers?.contains(data.userId) == true

                    if (isEditEvent && binding.friendCheckBox.isChecked) {
                        binding.friendCheckBox.isEnabled = false
                        binding.friendCheckBox.alpha = 0.5f
                    }



                    binding.friendCheckBox.apply { show() }.also {
                        it.setOnCheckedChangeListener { compoundButton, isChecked ->
                            buttonAction(listener, data)
                        }
                    }

                } else {
                    binding.btnMessage.hide()
                    binding.txtRequestCanceled.show()
                }


            }
            else -> {}
        }
    }

    private fun buttonAction(
        listener: FeedRequestAdapterListener<FeedRequestUserData>,
        data: FeedRequestUserData,
    ) {
        listener.onActionRequest(
            RequestKeyStatus.MESSAGE,
            position,
            data,
            FeedKeyStatus.IS_FRIEND
        )
    }

}


class RequestUpdateViewHolderForRequests(
    val binding: com.friendzrandroid.databinding.ItemFeedSmallButtonBinding,
    private val isInbox: Boolean = false,
    private val isGroupChat: Boolean = false,
    private val isInRequests: Boolean = false
) :
    FeedRequestViewHolder(binding) {

    override fun bind(
        pageData: BasePagingModel<FeedRequestUserData>,
        position: Int,
        listener: FeedRequestAdapterListener<FeedRequestUserData>
    ) {
        when (pageData.type) {
            DataType.DATA -> {


                val data = pageData.data!!
                binding.userImage.loadImage(data.image)
                binding.displayUserNameTxt.text = data.userName
                binding.userNameTxt.text = "@${data.displayedUserName}"

                val token = UserSessionManagement.getKeyAuthToken()


                if (token != null && !token.equals("")) {
                    binding.tvFeedInterestsMatch.text =
                        "${data.interestMatchPercent}% interests match"

                    binding.feedInterestsMatchProgressBar.progress = data.interestMatchPercent
                } else {

                    binding.tvFeedInterestsMatch.hide()

                    binding.feedInterestsMatchProgressBar.hide()
                }

                if (isInRequests)
                    binding.requestDateTxt.text = data.regestdata

                if (data.key == FeedKeyStatus.IS_FRIEND.key && !isInbox && !isGroupChat) {

                    binding.btnMessage.show()
                    binding.txtRequestCanceled.hide()
                    binding.btnMessage.setOnClickListener { buttonAction(listener, data) }

                } else if (data.key == FeedKeyStatus.IS_FRIEND.key && isInbox && !isGroupChat) {

                    binding.btnMessage.hide()
                    binding.txtRequestCanceled.hide()
                    binding.friendDataContainer.setOnClickListener { buttonAction(listener, data) }

                } else if (data.key == FeedKeyStatus.IS_FRIEND.key && isGroupChat) {

                    binding.btnMessage.hide()
                    binding.txtRequestCanceled.hide()
                    binding.friendCheckBox.apply { show() }.also {
                        it.setOnCheckedChangeListener { compoundButton, isChecked ->
                            buttonAction(listener, data)
                        }
                    }

                } else {
                    binding.btnMessage.hide()
                    binding.txtRequestCanceled.show()
                }


            }
            else -> {}
        }
    }

    private fun buttonAction(
        listener: FeedRequestAdapterListener<FeedRequestUserData>,
        data: FeedRequestUserData,
    ) {
        listener.onActionRequest(
            RequestKeyStatus.MESSAGE,
            position,
            data,
            FeedKeyStatus.IS_FRIEND
        )
    }

}