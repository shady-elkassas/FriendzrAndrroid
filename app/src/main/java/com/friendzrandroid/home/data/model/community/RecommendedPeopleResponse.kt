package com.friendzrandroid.home.data.model.community

data class RecommendedPeopleResponse(
    val userId: String,
    val name: String,
    val image: String,
    val interestMatchPercent: Double,
    val distanceFromYou: Double,
    val matchedInterests: List<String>
)
