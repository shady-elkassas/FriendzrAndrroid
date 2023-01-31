package com.friendzrandroid.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.*
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.databinding.ItemRecentConnectionBinding
import com.friendzrandroid.home.data.model.community.RecentlyConnectedItemData

import com.friendzrandroid.home.fragment.home.community.CommunityViewModel

class CommunityRecentConnectionAdapter(
    communityViewModel: CommunityViewModel,
    private val listener: BaseAdapterListener<RecentlyConnectedItemData>,
) : PagingAdapter<RecentlyConnectedItemData>(communityViewModel) {
//    private val profileDetailsListener: GoToProfileDetailsListener<RecentlyConnectedItemData>


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<RecentlyConnectedItemData> {
        val item = DataBindingUtil.inflate<ItemRecentConnectionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_recent_connection,
            parent,
            false
        )

        return ProfileDetailsViewHolder(item)
    }


    override fun onBindViewHolder(
        holder: BaseViewHolder<RecentlyConnectedItemData>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)

        val currentItem = adapterList.get(position)
        if (currentItem.type == DataType.LOADING) {
            holder.bind(adapterList.get(position))
        } else {
            adapterList.get(position).data?.let {

                holder.itemView.setOnClickListener {

                    listener.onItemSelected(adapterList.get(position).data!!)
                }


            }

            holder.bind(adapterList.get(position))

        }


    }

    inner class ProfileDetailsViewHolder(val view: ItemRecentConnectionBinding) :
        BaseViewHolder<RecentlyConnectedItemData>(view) {


        override fun bind(pageData: BasePagingModel<RecentlyConnectedItemData>) {

            when (pageData.type) {
                DataType.LOADING -> {
                    view.shimmerLoading.startShimmer()

                }

                DataType.DATA -> {
                    if (pageData.data != null) {
                        view.shimmerLoading.stopShimmer()
                        view.shimmerLoading.hideShimmer()

                        view.inboxImage.loadImage(pageData.data!!.image)
                        view.tvUserName.text = pageData.data!!.name

                        view.eventAttendeceValue.text = pageData.data!!.date


//                    pageData.data?.eventtypecolor?.toColorInt()
//                        ?.let { view.eventTypeColor.setBackgroundColor(it) }

//                    view.recentConnectionContainer.setOnClickListener {
//                        profileDetailsListener.goToProfileDetails(pageData.data!!)
//                    }

                    } else {
                        view.shimmerLoading.stopShimmer()
                        view.shimmerLoading.hideShimmer()

                    }
                }

            }

        }
    }

}