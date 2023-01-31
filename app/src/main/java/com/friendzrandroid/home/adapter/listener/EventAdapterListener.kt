package com.friendzrandroid.home.adapter.listener

import com.friendzrandroid.core.paggingList.BaseAdapterListener

interface EventAdapterListener<T> : BaseAdapterListener<T> {
    fun onItemEditButtonPressed(data: T)
}