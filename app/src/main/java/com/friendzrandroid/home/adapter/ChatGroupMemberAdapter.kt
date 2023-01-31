package com.friendzrandroid.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.ItemEventAttendeceBinding
import com.friendzrandroid.home.data.model.groupchat.GroupChatSubscriber

class ChatGroupMemberAdapter(
    private val listener: GroupAttendanceCallBack? = null,
    val isGroupAdmin: Boolean = false
) : RecyclerView.Adapter<ChatGroupMemberAdapter.ViewHolder>() {

    val mList = ArrayList<GroupChatSubscriber>()

    fun setList(data: List<GroupChatSubscriber>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    fun removeUser(position: Int) {
        mList.removeAt(position)
        notifyDataSetChanged()
    }

    fun showLoading() {
        mList.add(
            GroupChatSubscriber(
                "",
                "",
                "",
                1,
                1,
                "",
                "",
                false,
                ""
            )
        )

        mList.add(
            GroupChatSubscriber(
                "",
                "",
                "",
                1,
                1,
                "",
                "",
                false,
                ""
            )
        )

        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatGroupMemberAdapter.ViewHolder {
        val item = DataBindingUtil.inflate<ItemEventAttendeceBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_event_attendece,
            parent,
            false
        )
        return ViewHolder(item)
    }

    inner class ViewHolder(val view: ItemEventAttendeceBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun bind(data: GroupChatSubscriber, position: Int) {

            if (data.isAdminGroup) {

                view.imageViewMenu.hide()
                view.eventAdminText.show()

            } else if (isGroupAdmin) {

                view.eventAdminText.hide()
                view.imageViewMenu.show()

                view.imageViewMenu.setOnClickListener {
                    listener?.onActionTapper(
                        it,
                        data.userId,
                        position
                    )
                }
            } else {
                view.eventAdminText.hide()
                view.imageViewMenu.hide()
            }

            if (data.userName.isEmpty() && data.image.isEmpty()) {
                view.shimmerBack.show()
                view.shimmerLoading.startShimmer()
            } else {
                view.shimmerLoading.hideShimmer()
                view.shimmerBack.hide()
                view.eventUserImage.loadImage(data.image)
                view.eventUserName.setText(data.userName)
            }

        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position], position)
    }

    override fun getItemCount(): Int = mList.size


    interface GroupAttendanceCallBack {
        fun onActionTapper(btnView: View, userId: String, position: Int)
    }
}