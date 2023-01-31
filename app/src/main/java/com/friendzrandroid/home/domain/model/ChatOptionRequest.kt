package com.friendzrandroid.home.domain.model

data class ChatOptionRequest(
    val id: String,
    val mute: Boolean? = null,
    val isEvent: Boolean? = null,
    val DeleteDateTime: String? = null
)
