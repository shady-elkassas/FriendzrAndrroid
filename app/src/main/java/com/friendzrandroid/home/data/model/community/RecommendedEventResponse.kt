package com.friendzrandroid.home.data.model.community

data class RecommendedEventResponse(
    val eventId: String,
    val title: String,
    val description: String,
    val image: String,
    val eventtype: String,
    val eventColor: String,
    val eventtypecolor: String,
    val attendees: String,
    val from: String,
    val eventDate: String,
    val interestMatchPercent: Double,
    val distanceFromYou: Double,
    val matchedInterests: List<String>
)
