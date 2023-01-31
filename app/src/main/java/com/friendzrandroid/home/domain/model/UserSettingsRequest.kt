package com.friendzrandroid.home.domain.model

data class UserSettingsRequest(
    val pushnotification: Boolean? = null,
    val allowmylocation: Boolean? = null,
    val ghostmode: Boolean? = null,
    val manualdistancecontrol: Double? = null,
    val filteringaccordingtoage: Boolean? = null,
    val agefrom: Int? = null,
    val ageto: Int? = null,
    val myAppearanceTypes: String,
    val personalSpace: Boolean? = null,
    val distanceFilter: Boolean? = null,
    val language: String? = "ar"
)