package com.friendzrandroid.home.dialog.eventsDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.databinding.ItemDialogEventBinding
import com.friendzrandroid.databinding.ItemRequestBinding
import com.friendzrandroid.databinding.ItemTagDialogBinding
import com.friendzrandroid.home.data.model.EventMapData
import com.friendzrandroid.home.data.model.EventMapResponseItem
import com.friendzrandroid.home.data.model.InterestData
import com.friendzrandroid.home.data.model.TagsModel

class EventsDialogAdapter(val container:EventsDialog, val data: List<EventMapData>,val listener:onEventSelected): RecyclerView.Adapter<EventsDialogAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = DataBindingUtil.inflate<ItemDialogEventBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_dialog_event,
            parent,
            false
        )

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tagView.root.setOnClickListener {
            listener.EventData(container,data.get(position))
        }
        holder.bind(data.get(position))
    }

    override fun getItemCount() = data.size


    inner class ViewHolder(val tagView: ItemDialogEventBinding) :
        RecyclerView.ViewHolder(tagView.root) {

        fun bind(data: EventMapData) {
            tagView.eventImage.loadImage(data.image)
            tagView.eventTitle.text = data.title
            tagView.eventTo.text = data.eventdateto
            tagView.eventAttendece.text = "${tagView.root.context.resources.getString(R.string.attendance)}: ${data.joined}/${data.totalnumbert}"
        }

    }

}