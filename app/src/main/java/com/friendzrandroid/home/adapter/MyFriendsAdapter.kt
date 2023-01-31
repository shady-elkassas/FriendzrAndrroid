package com.friendzrandroid.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.BaseViewHolder
import com.friendzrandroid.core.paggingList.DataType
import com.friendzrandroid.core.paggingList.PagingAdapter
import com.friendzrandroid.databinding.ItemFeedBinding
import com.friendzrandroid.databinding.ItemRequestBinding
import com.friendzrandroid.databinding.ItemRequestUpdateBinding
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.adapter.sharedViewHolder.FeedRequestViewHolder
import com.friendzrandroid.home.adapter.sharedViewHolder.FeedViewHolder
import com.friendzrandroid.home.adapter.sharedViewHolder.RequestUpdateViewHolder
import com.friendzrandroid.home.adapter.sharedViewHolder.RequestViewHolder
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.fragment.home.messages.allFriends.AllFriendsViewModel

class MyFriendsAdapter(
    viewModel: AllFriendsViewModel,
    private val listener: FeedRequestAdapterListener<FeedRequestUserData>,
    private val isGroupChatFragment: Boolean = false,
) :
    PagingAdapter<FeedRequestUserData>(viewModel) {
//    init {
//        viewModel.loadData()
//    }

    fun updateItem(position: Int, newKey: Int) {

        adapterList[position].data!!.key = newKey

        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<FeedRequestUserData> {


        when (viewType) {


            FeedKeyStatus.OTHER_USER_SEND_REQUEST.key -> {// inflate item_request

                val item = DataBindingUtil.inflate<ItemRequestBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_request,
                    parent,
                    false
                )

                return RequestViewHolder(item)
            }

            FeedKeyStatus.IS_FRIEND.key,
            FeedKeyStatus.UPDATE_DECLINED_REQUEST.key -> { // inflate item_request_update

                val item = DataBindingUtil.inflate<ItemRequestUpdateBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_request_update,
                    parent,
                    false
                )

                return RequestUpdateViewHolder(
                    item,
                    isInbox = true,
                    isGroupChat = isGroupChatFragment
                )

            }

            FeedKeyStatus.YOU_SENT_REQUEST.key,
            FeedKeyStatus.NORMAL_FEED_STATE.key -> {
                val item = DataBindingUtil.inflate<ItemFeedBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_feed,
                    parent,
                    false
                )

                return FeedViewHolder(item)
            }

            FeedKeyStatus.YOU_BLOCK_USER.key -> {
                Log.e("TAG", "onCreateViewHolder: Blocked")
                val item = DataBindingUtil.inflate<ItemRequestUpdateBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_request_update,
                    parent,
                    false
                )

                return RequestUpdateViewHolder(item)
            }

            else -> {
                val item = DataBindingUtil.inflate<ItemFeedBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_feed,
                    parent,
                    false
                )

                return FeedRequestViewHolder(item)
            }


        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<FeedRequestUserData>, position: Int) {
        super.onBindViewHolder(holder, position)
        val currentItem = adapterList.get(position)
        if (currentItem.type == DataType.LOADING) {
            holder.bind(adapterList.get(position))
        } else {
            holder.itemView.setOnClickListener {
                listener.onItemSelected(currentItem.data!!)
            }
            holder.bind(adapterList.get(position), position, listener)

        }

    }


    override fun getItemViewType(position: Int): Int {
        if (adapterList.get(position).data == null) {
            return FeedKeyStatus.BASE_STATE.key
        } else
            return adapterList.get(position).data!!.key
    }


}