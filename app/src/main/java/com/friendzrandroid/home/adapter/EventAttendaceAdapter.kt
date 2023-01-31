package com.friendzrandroid.home.adapter

import android.util.Log
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
import com.friendzrandroid.home.data.model.AttendenceData

class EventAttendaceAdapter(
    private val listener: EventAttendanceCallBack? = null,
    val isFromEdit: Boolean = true,
    val isEventAdmin: Boolean = false
) : RecyclerView.Adapter<EventAttendaceAdapter.ViewHolder>() {

    private val mList = ArrayList<AttendenceData>()


    fun showLoading() {
        mList.add(
            AttendenceData(
                "",
                "",
                arrayListOf(),
                "",
                1,
                "",
                false
            )
        )
        mList.add(
            AttendenceData(
                "",
                "",
                arrayListOf(),
                "",
                1,
                "",
                false
            )
        )
        mList.add(
            AttendenceData(
                "",
                "",
                arrayListOf(),
                "",
                1,
                "",
                false
            )
        )
        mList.add(
            AttendenceData(
                "",
                "",
                arrayListOf(),
                "",
                1,
                "",
                false
            )
        )
        mList.add(
            AttendenceData(
                "",
                "",
                arrayListOf(),
                "",
                1,
                "",
                false
            )
        )
        notifyDataSetChanged()
    }

    fun setList(data: List<AttendenceData>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = DataBindingUtil.inflate<ItemEventAttendeceBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_event_attendece,
            parent,
            false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList.get(position))
    }

    override fun getItemCount() = mList.size


    inner class ViewHolder(val view: ItemEventAttendeceBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun bind(data: AttendenceData) {

            Log.i("TAG EventAttende", "bind: $data")

            if (data.userName.isEmpty() && data.image.isEmpty()) {
                view.shimmerBack.show()
                view.shimmerLoading.startShimmer()
            } else {
                view.shimmerLoading.hideShimmer()
                view.shimmerBack.hide()
                view.eventUserImage.loadImage(data.image)
                view.eventUserName.setText(data.userName)

                if (isFromEdit) {
                    if (data.myEvent) {
                        view.imageViewMenu.hide()
                        view.eventAdminText.show()
                    } else {
                        if (isEventAdmin) {
                            view.actionBtnContainer.show()
                            view.actionBtnContainer.setOnClickListener {
                                listener?.onActionTapper(
                                    view.imageViewMenu,
                                    data
                                )
                            }
                        } else
                            view.actionBtnContainer.hide()
                    }



//                    if (data.myEvent) {
//                        Log.i("TAG EventAttende", "bind: Here1")
//                        view.actionBtnContainer.show()
//                        view.actionBtnContainer.setOnClickListener {
//                            listener?.onActionTapper(
//                                view.imageViewMenu,
//                                data
//                            )
//                        }
//                    } else {
//                        Log.i("TAG EventAttende", "bind: Here2")
//                        view.imageViewMenu.hide()
//                        view.eventAdminText.show()
//                    }

                }
            }

        }

    }

    interface EventAttendanceCallBack {
        fun onActionTapper(btnView: View, eventAttend: AttendenceData)
    }
}