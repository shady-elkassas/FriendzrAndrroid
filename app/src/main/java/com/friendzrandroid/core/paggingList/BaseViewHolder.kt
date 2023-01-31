package com.friendzrandroid.core.paggingList

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener

abstract class BaseViewHolder<T>(view:ViewDataBinding):RecyclerView.ViewHolder(view.root) {

    open fun bind(pageData:BasePagingModel<T>){}
    open fun bind(pageData:BasePagingModel<T>,position: Int){}
    open fun bind(pageData:BasePagingModel<T>,position: Int,listener: FeedRequestAdapterListener<T>){}
}