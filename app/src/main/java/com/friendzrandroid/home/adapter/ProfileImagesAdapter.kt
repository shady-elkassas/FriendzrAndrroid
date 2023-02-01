package com.friendzrandroid.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.friendzrandroid.databinding.ItemProfileImageBinding

class ProfileImagesAdapter(
    private val imageUrlList: List<String>,
    val onImageClicked: (String) -> Unit
) : RecyclerView.Adapter<ProfileImagesAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(val binding: ItemProfileImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(imageUrl: String, onImageClicked: (String) -> Unit) {

            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.userProfileImage)

            binding.userProfileImage.setOnClickListener {
                onImageClicked.invoke(imageUrl)
            }
        }

    }

    override fun getItemCount(): Int = imageUrlList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = ItemProfileImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(imageUrlList[position], onImageClicked)
    }

}
