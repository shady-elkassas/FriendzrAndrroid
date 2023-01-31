package com.friendzrandroid.home.adapter.listener

import com.friendzrandroid.core.paggingList.BaseAdapterListener

interface GoToProfileDetailsListener<T>: BaseAdapterListener<T> {
    fun goToProfileDetails(data: T)

}