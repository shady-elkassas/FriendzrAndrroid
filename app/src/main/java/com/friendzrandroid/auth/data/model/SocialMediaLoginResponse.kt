package com.friendzrandroid.auth.data.model


data class SocialMediaLoginResponse(
    val `data`: Data,
    val errorMessage: String,
    val isSuccess: Boolean
) {
    data class Data(
        val email: String,
        val name: String,
        val token: String
    )
}