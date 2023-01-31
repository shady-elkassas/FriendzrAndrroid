package com.friendzrandroid.home.data.model

data class UserSettingsResponse(
    val agefrom: Int,
    val ageto: Int,
    val myAppearanceTypes: ArrayList<Int>,
    val allowmylocation: Boolean,
    val filteringaccordingtoage: Boolean,
    val ghostmode: Boolean,
    val language: Any?,
    val manualdistancecontrol: Double,
    val pushnotification: Boolean,
    val personalSpace: Boolean,
    val distanceFilter: Boolean


)