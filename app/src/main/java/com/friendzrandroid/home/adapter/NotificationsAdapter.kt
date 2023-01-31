package com.friendzrandroid.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.BasePagingModel
import com.friendzrandroid.core.paggingList.BaseViewHolder
import com.friendzrandroid.core.paggingList.DataType
import com.friendzrandroid.core.paggingList.PagingAdapter
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.databinding.ItemInboxBinding
import com.friendzrandroid.databinding.ItemNotificationBinding
import com.friendzrandroid.home.data.model.NotificationData
import com.friendzrandroid.home.fragment.more.notification.NotificationViewModel

class NotificationsAdapter(viewmodel: NotificationViewModel) : PagingAdapter<NotificationData>(viewmodel) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<NotificationData> {
        val item = DataBindingUtil.inflate<ItemNotificationBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_notification,
            parent,
            false
        )

        return InboxViewHolder(item)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<NotificationData>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(adapterList.get(position))
    }




    inner class InboxViewHolder(val view:ItemNotificationBinding):BaseViewHolder<NotificationData>(view){
        override fun bind(pageData: BasePagingModel<NotificationData>) {
           when(pageData.type){
               DataType.LOADING ->{
                   view.shimmerLoading.startShimmer()
                   view.ShimmerDisplayMessage.startShimmer()
                   view.ShimmerDisplayTitleChat.startShimmer()
               }
               DataType.DATA ->{
                   view.shimmerLoading.hideShimmer()
                   view.ShimmerDisplayMessage.hideShimmer()
                   view.ShimmerDisplayTitleChat.hideShimmer()
                   view.ShimmerDisplayMessage.hide()
                   view.ShimmerDisplayTitleChat.hide()


                   view.inboxTitle.text = pageData.data!!.title
                   view.inboxImage.loadImage(pageData.data!!.imageUrl)
                   view.inboxMessage.text = pageData.data!!.body
                   view.inboxTime.text = pageData.data!!.createdAt
               }
           }
        }
    }
}