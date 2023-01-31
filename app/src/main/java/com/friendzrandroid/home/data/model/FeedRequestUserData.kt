package com.friendzrandroid.home.data.model

data class FeedRequestUserData(
    val displayedUserName: String,
    val email: String,
    val gender: String,
    val image: String,
    var key: Int,
    var interestMatchPercent: Int,
    val lang: Any,
    val lat: Any,
    val userId: String,
    val userName: String,
    val regestdata: String
)