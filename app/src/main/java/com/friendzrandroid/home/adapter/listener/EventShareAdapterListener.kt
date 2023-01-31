package com.friendzrandroid.home.adapter.listener

import com.friendzrandroid.home.data.model.enum.ShareTypeState

interface EventShareAdapterListener {
    fun onSendButtonPressed(idToShareTo: String, shareToState: ShareTypeState, position: Int)
}