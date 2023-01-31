package com.friendzrandroid.home.dialog.tagsDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.friendzrandroid.R
import com.friendzrandroid.databinding.ItemRequestBinding
import com.friendzrandroid.databinding.ItemTagDialogBinding
import com.friendzrandroid.home.data.model.InterestData
import com.friendzrandroid.home.data.model.TagsModel

class TagsDialogAdapter: RecyclerView.Adapter<TagsDialogAdapter.ViewHolder>() {

    val tagList = ArrayList<InterestData>()

    fun updateList(data: ArrayList<InterestData>) {
        tagList.clear()
        tagList.addAll(data)
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = DataBindingUtil.inflate<ItemTagDialogBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_tag_dialog,
            parent,
            false
        )

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tagView.root.setOnClickListener {
//            tagList[position].isSelected = !tagList[position].isSelected
            notifyItemChanged(position)
        }
        holder.bind(tagList.get(position))
    }

    override fun getItemCount() = tagList.size


    inner class ViewHolder(val tagView: ItemTagDialogBinding) :
        RecyclerView.ViewHolder(tagView.root) {

        fun bind(data: InterestData) {
//            tagView.tagCheck.isChecked = data.isSelected
            tagView.tagTxt.text = data.name
        }

    }

}