package com.friendzrandroid.home.data.model


import com.google.gson.annotations.SerializedName

data class UserSettingsData(
    @SerializedName("isSuccessful")
    val isSuccessful: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("model")
    val model: Model,
    @SerializedName("statusCode")
    val statusCode: Int
) {
    data class Model(
        @SerializedName("agefrom")
        val agefrom: Int,
        @SerializedName("ageto")
        val ageto: Int,
        @SerializedName("allowMyAppearanceType")
        val allowMyAppearanceType: Int,
        @SerializedName("allowmylocation")
        val allowmylocation: Boolean,
        @SerializedName("filteringaccordingtoage")
        val filteringaccordingtoage: Boolean,
        @SerializedName("ghostmode")
        val ghostmode: Boolean,
        @SerializedName("language")
        val language: Any,
        @SerializedName("manualdistancecontrol")
        val manualdistancecontrol: Double,
        @SerializedName("pushnotification")
        val pushnotification: Boolean
    )
}