package com.friendzrandroid.auth.data.model


data class LoginResponseData(
    val agefrom: Int,
    val ageto: Int,
    val frindRequestNumber: Int,
    val message_Count: Int,
    val notificationcount: Int,
    val myAppearanceTypes: List<Int>,
    val allowmylocation: Boolean,
    val isWhiteLable: Boolean,
    val code: String,
    val email: String,
    val filteringaccordingtoage: Boolean,
    val ghostmode: Boolean,
    val language: String,
    val manualdistancecontrol: Float,
    val needUpdate: Int,
    val phoneNumber: String,
    val pushnotification: Boolean,
    val token: String,
    val userID: String,
    val userImage: String,
    val userName: String,
    val displayedUserName: String,

    val interests: List<String>



)


