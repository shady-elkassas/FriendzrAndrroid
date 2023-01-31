package com.friendzrandroid.home.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.*
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.ItemInboxBinding
import com.friendzrandroid.home.data.model.InboxData
import com.friendzrandroid.home.data.model.enum.MessageTypeEnum
import com.friendzrandroid.home.fragment.home.messages.inbox.InboxViewModel
import java.text.SimpleDateFormat

class InboxAdapter(viewmodel: InboxViewModel, val listener: BaseAdapterListener<InboxData>) :
    PagingAdapter<InboxData>(viewmodel) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<InboxData> {
        val item = DataBindingUtil.inflate<ItemInboxBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_inbox,
            parent,
            false
        )

        return NotificationViewHolder(item)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<InboxData>, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.bind(adapterList.get(position))
    }


    inner class NotificationViewHolder(val view: ItemInboxBinding) :
        BaseViewHolder<InboxData>(view) {
        override fun bind(pageData: BasePagingModel<InboxData>) {
            when (pageData.type) {
                DataType.LOADING -> {
                    view.shimmerLoading.startShimmer()
                    view.ShimmerDisplayMessage.startShimmer()
                    view.ShimmerDisplayTitleChat.startShimmer()
                }

                DataType.DATA -> {
                    if (pageData.data != null) {
                        view.shimmerLoading.hideShimmer()
                        view.ShimmerDisplayMessage.hideShimmer()
                        view.ShimmerDisplayTitleChat.hideShimmer()
                        view.ShimmerDisplayMessage.hide()
                        view.ShimmerDisplayTitleChat.hide()

                        view.inboxTitle.text = pageData.data!!.name
                        view.inboxImage.loadImage(pageData.data!!.image)


                        val format = SimpleDateFormat("dd-MM-yyyy")
                        val currentDate = format.parse(pageData.data!!.latestdate)

                        var isToday = false
                        var isYesterday = false
                        currentDate?.let {
                            isToday = DateUtils.isToday(it.time)
                            isYesterday =
                                DateUtils.isToday(currentDate.time + DateUtils.DAY_IN_MILLIS)
                        }

                        if (!isToday && !isYesterday)
                            view.inboxTime.text =
                                "${pageData.data!!.latestdate} ${pageData.data!!.latesttime}"
                        else if (isToday)
                            view.inboxTime.text =
                                "Today ${pageData.data!!.latesttime}"
                        else if (isYesterday)
                            view.inboxTime.text =
                                "Yesterday ${pageData.data!!.latesttime}"


                        view.root.setOnClickListener {
                            listener.onItemSelected(pageData.data!!)
                        }

                        if (pageData.data!!.muit)
                            view.muteIcon.show()
                        else
                            view.muteIcon.hide()


                        if (pageData.data!!.message_not_Read > 0) {
                            view.chatBadgeContainer.show()
                            view.chatBadge.text = pageData.data!!.message_not_Read.toString()
                        } else
                            view.chatBadgeContainer.hide()





                        if (pageData.data!!.messagestype == MessageTypeEnum.SHARE.value) {
                            view.inboxMessage.text = "Event"
                        } else if (pageData.data!!.messagestype == MessageTypeEnum.IMAGE.value) {
                            view.inboxMessageImage.show()
                            view.inboxMessageImageTypeImage.show()
                            view.inboxMessageImageTypeFile.hide()
                            view.inboxMessageImageType.text = "Photo"
                            view.inboxMessage.hide()


                        } else if (pageData.data!!.messagestype == MessageTypeEnum.FILE.value) {
                            view.inboxMessageImage.show()
                            view.inboxMessageImageTypeImage.hide()
                            view.inboxMessageImageTypeFile.show()
                            view.inboxMessageImageType.text = "File"

                            view.inboxMessage.hide()
                        } else {
                            view.inboxMessageImage.hide()
                            view.inboxMessage.show()
                            view.inboxMessage.text = pageData.data!!.messages

                        }


                    } else {
                        view.shimmerLoading.stopShimmer()
                        view.ShimmerDisplayMessage.stopShimmer()
                        view.ShimmerDisplayTitleChat.stopShimmer()
                        view.ShimmerDisplayMessage.hide()
                        view.ShimmerDisplayTitleChat.hide()
                    }
                }


            }
        }
    }
}