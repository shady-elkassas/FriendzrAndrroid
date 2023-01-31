package com.friendzrandroid.home.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.friendzrandroid.R


class AdditionalImagesAdapter(private val list: List<Uri>) : RecyclerView.Adapter<AdditionalImagesAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View =
            inflater.inflate(com.friendzrandroid.R.layout.item_additional_images, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.imageView.setImageURI(list[position])
    }

    override fun getItemCount(): Int {
        return list.size

    }

    class ItemViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.imgAdditionalImage)

    }




}