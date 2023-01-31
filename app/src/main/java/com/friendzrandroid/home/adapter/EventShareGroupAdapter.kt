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
import com.friendzrandroid.home.data.model.InboxData
import com.friendzrandroid.home.data.model.enum.ShareTypeState
import com.friendzrandroid.home.fragment.home.events.eventShare.EventShareGroupViewModel

class EventShareGroupAdapter(
    viewModel: EventShareGroupViewModel,
    val activity: Activity,
    val listener: EventShareAdapterListener
) : PagingAdapter<InboxData>(viewModel) {

    var isShareDone: String = "Send"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<InboxData> {

        val item = DataBindingUtil.inflate<ItemShareBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_share,
            parent,
            false
        )

        return EventShareViewHolder(item)

    }

    override fun onBindViewHolder(holder: BaseViewHolder<InboxData>, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.bind(adapterList[position])

    }

    inner class EventShareViewHolder(val view: ItemShareBinding) :
        BaseViewHolder<InboxData>(view) {

        override fun bind(pageData: BasePagingModel<InboxData>) {

            when (pageData.type) {
                DataType.LOADING -> {}
                DataType.DATA -> {
                    view.shareToName.text = pageData.data?.name

                    view.shareToButton.text = isShareDone
                    view.shareToButton.setOnClickListener {
                        view.shareToButton.text = activity.resources.getText(R.string.title_sending)
                        listener.onSendButtonPressed(
                            pageData.data?.id!!,
                            ShareTypeState.SHARE_TO_GROUP,
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

//    private fun changeButtonState(view: ItemShareBinding) {
//        if (isShareDone)
//            view.shareToButton.hideButtonLoading(
//                activity.resources.getString(R.string.title_sent),
//                activity
//            ) {
//                view.shareToButton.setBackgroundResource(R.drawable.shape_request_sent)
//            }
//        else
//            view.shareToButton.showButtonLoading(activity.resources.getString(R.string.send))
//    }
//
//    fun updateButtonState(position: Int, isDone: Boolean) {
//        isShareDone = isDone
//        notifyItemChanged(position)
//    }
}