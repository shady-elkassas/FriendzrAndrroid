package com.friendzrandroid.home.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.BasePagingModel
import com.friendzrandroid.core.paggingList.BaseViewHolder
import com.friendzrandroid.core.paggingList.DataType
import com.friendzrandroid.core.paggingList.PagingAdapter
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.core.utils.showButtonLoading
import com.friendzrandroid.databinding.ItemBlockBinding
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import com.friendzrandroid.home.fragment.more.blockList.BlockListViewModel

class BlockListAdapter(
    viewModel: BlockListViewModel,
    private val listener: FeedRequestAdapterListener<FeedRequestUserData>,
) : PagingAdapter<FeedRequestUserData>(viewModel) {

    var isRequestSuccess: Boolean = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<FeedRequestUserData> {


        val item = DataBindingUtil.inflate<ItemBlockBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_block,
            parent,
            false
        )

        return BlockViewHolder(item)


    }

    override fun onBindViewHolder(holder: BaseViewHolder<FeedRequestUserData>, position: Int) {
        super.onBindViewHolder(holder, position)
        val currentItem = adapterList.get(position)
        if (currentItem.type == DataType.LOADING) {
            holder.bind(adapterList.get(position))
        } else {

            holder.bind(adapterList[position], position, listener)

        }

    }


//    override fun getItemViewType(position: Int): Int {
//        if (adapterList.get(position).data == null) {
//            return FeedKeyStatus.BASE_STATE.key
//        } else
//            return adapterList.get(position).data!!.key
//    }


    inner class BlockViewHolder(val view: ItemBlockBinding) :
        BaseViewHolder<FeedRequestUserData>(view) {

        override fun bind(
            pageData: BasePagingModel<FeedRequestUserData>,
            position: Int,
            listener: FeedRequestAdapterListener<FeedRequestUserData>
        ) {

            pageData.data?.let {

                view.shimmerLoading.stopShimmer()
                view.shimmerLoading.hideShimmer()

                view.cimgFeedUserImage.loadImage(it.image)
                view.tvFeedItemTitle.text = it.userName


                view.btnUnBlock.apply {
                    setOnClickListener {
                        listener.onActionRequest(
                            RequestKeyStatus.UN_BLOCK,
                            position,
                            pageData.data!!,
                            FeedKeyStatus.NORMAL_FEED_STATE
                        )

                        showButtonLoading(resources.getString(R.string.title_un_blocking))
                    }
                }
            }
        }
    }

}