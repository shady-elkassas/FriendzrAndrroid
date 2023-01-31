package com.friendzrandroid.home.adapter.listener

import com.friendzrandroid.core.paggingList.BaseAdapterListener
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus

interface FeedRequestAdapterListener<T>: BaseAdapterListener<T> {
    fun onActionRequest(newStatus:RequestKeyStatus, position:Int, data: FeedRequestUserData,afterSuccessStatus:FeedKeyStatus)

}