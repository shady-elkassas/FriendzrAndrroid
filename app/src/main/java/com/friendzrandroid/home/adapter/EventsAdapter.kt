package com.friendzrandroid.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.BasePagingModel
import com.friendzrandroid.core.paggingList.BaseViewHolder
import com.friendzrandroid.core.paggingList.DataType
import com.friendzrandroid.core.paggingList.PagingAdapter
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.databinding.ItemEventsBinding
import com.friendzrandroid.home.adapter.listener.EventAdapterListener
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.data.model.enum.EventStates
import com.friendzrandroid.home.fragment.home.events.eventList.EventsViewModel

class EventsAdapter(
    viewModel: EventsViewModel,
    private val listener: EventAdapterListener<EventItemData>
) : PagingAdapter<EventItemData>(viewModel) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<EventItemData> {
        val item = DataBindingUtil.inflate<ItemEventsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_events,
            parent,
            false
        )

        return EventViewHolder(item)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<EventItemData>, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.bind(adapterList.get(position))
        adapterList.get(position).data?.let {
            holder.itemView.setOnClickListener {
                listener.onItemSelected(adapterList.get(position).data!!)
            }
        }
    }

    inner class EventViewHolder(val view: ItemEventsBinding) : BaseViewHolder<EventItemData>(view) {

        override fun bind(pageData: BasePagingModel<EventItemData>) {

            when (pageData.type) {
                DataType.LOADING -> {
                    view.shimmerLoading.startShimmer()
                }

                DataType.DATA -> {

                    if (pageData.data?.key == EventStates.MINE.key) {
                        view.eventEditButton.visibility = View.VISIBLE

                        if (pageData.data!!.eventHasExpired == true) {
                            view.eventEditButton.visibility = View.INVISIBLE

                        } else {
                            view.eventEditButton.visibility = View.VISIBLE

                        }
                    }





                    view.shimmerLoading.hideShimmer()
                    view.eventImage.loadImage(pageData.data!!.image)
                    view.eventTitle.text = pageData.data!!.title
                    view.eventCategory.text = pageData.data!!.categorie
                    view.tvEventsEventDateAndTime.text =
                        "${pageData.data!!.eventdate}"
                    view.tvEventsAttendance.text =
                        "${view.root.context.resources.getString(R.string.title_attendance)} ${pageData.data!!.joined} / ${pageData.data!!.totalnumbert}"

                    view.eventEditButton.setOnClickListener {
                        listener.onItemEditButtonPressed(pageData.data!!)
                    }
                }

            }

        }
    }


//
//    private inner class FeedViewHolder(val binding: ItemFeedBinding) :
//        BaseViewHolder(binding.root) {
//
//        override fun bind(position: Int, data: FeedRequestUserData) {
//            binding.cimgFeedUserImage.loadImage(data.image)
//            binding.tvFeedItemTitle.text = data.displayedUserName
//            binding.tvFeedUserName.text = "@${data.userName}"
//
//            if (data.key == FeedKeyStatus.NORMAL_FEED_STATE.key) {
//                binding.btnSendRequest.background =
//                    ContextCompat.getDrawable(binding.root.context, R.drawable.shape_send_request)
//                binding.btnSendRequest.setTextColor(
//                    ContextCompat.getColor(
//                        binding.root.context,
//                        R.color.white
//                    )
//                )
//                binding.btnSendRequest.text = binding.root.context.getText(R.string.sendRequest)
//
//                binding.btnSendRequest.setOnClickListener {
//                    listener.onActionRequest(
//                        RequestKeyStatus.SEND,
//                        position,
//                        data,
//                        FeedKeyStatus.YOU_SENT_REQUEST
//                    )
//                }
//
//            } else {
//                binding.btnSendRequest.background =
//                    ContextCompat.getDrawable(binding.root.context, R.drawable.shape_request_sent)
//                binding.btnSendRequest.setTextColor(
//                    ContextCompat.getColor(
//                        binding.root.context,
//                        R.color.primary_color
//                    )
//                )
//                binding.btnSendRequest.text = binding.root.context.getText(R.string.request_Sent)
//
//                binding.btnSendRequest.setOnClickListener {
//                    listener.onActionRequest(
//                        RequestKeyStatus.CANCEL_REJECT,
//                        position,
//                        data,
//                        FeedKeyStatus.NORMAL_FEED_STATE
//                    )
//                }
//            }
//
//
//        }
//    }
//
//
//    private inner class RequestViewHolder(val binding: ItemRequestBinding) :
//        BaseViewHolder(binding.root) {
//
//        override fun bind(position: Int, data: FeedRequestUserData) {
//            binding.userImage.loadImage(data.image)
//            binding.displayUserNameTxt.text = data.displayedUserName
//            binding.userNameTxt.text = "@${data.userName}"
//            binding.btnAcceptRequest.setOnClickListener {
//                listener.onActionRequest(
//                    RequestKeyStatus.SEND,
//                    position,
//                    data,
//                    FeedKeyStatus.IS_FRIEND
//                )
//            }
//            binding.btnDeclineRequest.setOnClickListener {
//                listener.onActionRequest(
//                    RequestKeyStatus.CANCEL_REJECT,
//                    position,
//                    data,
//                    FeedKeyStatus.UPDATE_DECLINED_REQUEST
//                )
//
//            }
//        }
//    }
//
//    private inner class RequestUpdateViewHolder(val binding: ItemRequestUpdateBinding) :
//        BaseViewHolder(binding.root) {
//
//        override fun bind(position: Int, data: FeedRequestUserData) {
//            binding.userImage.loadImage(data.image)
//            binding.displayUserNameTxt.text = data.displayedUserName
//            binding.userNameTxt.text = "@${data.userName}"
//
//            if (data.key == FeedKeyStatus.IS_FRIEND.key) {
//                binding.btnMessage.show()
//                binding.txtRequestCanceled.hide()
//            } else {
//                binding.btnMessage.hide()
//                binding.txtRequestCanceled.show()
//            }
//
//        }
//    }


}