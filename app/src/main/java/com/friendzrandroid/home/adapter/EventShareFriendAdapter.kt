package com.friendzrandroid.home.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.BasePagingModel
import com.friendzrandroid.core.paggingList.BaseViewHolder
import com.friendzrandroid.core.paggingList.DataType
import com.friendzrandroid.core.paggingList.PagingAdapter
import com.friendzrandroid.databinding.ItemShareBinding
import com.friendzrandroid.home.adapter.listener.EventShareAdapterListener
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.ShareTypeState
import com.friendzrandroid.home.fragment.home.events.eventShare.EventShareFriendViewModel

class EventShareFriendAdapter(
    viewModel: EventShareFriendViewModel,
    val activity: Activity,
    val listener: EventShareAdapterListener
) :
    PagingAdapter<FeedRequestUserData>(viewModel) {

    var isShareDone: String = "Send"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<FeedRequestUserData> {

        val item = DataBindingUtil.inflate<ItemShareBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_share,
            parent,
            false
        )

        return EventShareViewHolder(item)

    }

    override fun onBindViewHolder(holder: BaseViewHolder<FeedRequestUserData>, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.bind(adapterList[position])

    }

    inner class EventShareViewHolder(val view: ItemShareBinding) :
        BaseViewHolder<FeedRequestUserData>(view) {

        override fun bind(pageData: BasePagingModel<FeedRequestUserData>) {

            when (pageData.type) {
                DataType.LOADING -> {}
                DataType.DATA -> {
                    view.shareToName.text = pageData.data?.userName

                    view.shareToButton.text = isShareDone
                    view.shareToButton.setOnClickListener {
                        view.shareToButton.text = activity.resources.getText(R.string.title_sending)
                        listener.onSendButtonPressed(
                            pageData.data?.userId!!,
                            ShareTypeState.SHARE_TO_FRIEND,
                            position
                        )
                    }
                }
            }
        }
    }


    fun updateButtonState(position: Int, isDone: Boolean) {

        isShareDone = if (isDone) "Sent"
        else "Send"

        Log.e("Adapter", "updateButtonState: $position, isDone: $isShareDone")

        notifyItemChanged(position)
        //isShareDone = "Send"
    }
}