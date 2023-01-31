package com.friendzrandroid.home.domain.model

data class ChangeFeedStatusRequest(
    val userID: String,
    val key: Int,
    val isNotFriend: Boolean? = null
)