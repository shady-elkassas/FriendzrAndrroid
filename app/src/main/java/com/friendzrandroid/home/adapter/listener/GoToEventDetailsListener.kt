package com.friendzrandroid.home.adapter.listener

import com.friendzrandroid.core.paggingList.BaseAdapterListener

interface GoToEventDetailsListener<T>: BaseAdapterListener<T> {
    fun goToEventDetails(data: T)

}