package com.friendzrandroid.home.data.model

data class EventDetails(
    val allday: Boolean,
    val eventHasExpired: Boolean,
    val attendees: List<AttendenceData>,
    val categorie: String,
    val categorieid: Int,
    val categorieimage: String,
    val description: String,
    val entityId: String,
    val eventdate: String,
    val eventdateto: String,
    val genderStatistic: List<GenderStatistic>,
    val id: String,
    val image: String,
    val UserImage: String,
    val interestStatistic: List<InterestStatistic>,
    val joined: Int,
    val key: Int,
    val lang: String,
    val lat: String,
    val timefrom: String,
    val timeto: String,
    val title: String,
    val timetext: String,
    val totalnumbert: Int,
    val eventtype: String,
    val eventTypeName: String,
    val eventtypeid: String,
    val checkout_details: String


)


data class AttendenceData(
    val userId: String,
    val image: String,
    val interests: List<Interest>,
    val joinDate: String,
    val stutus: Int,
    val userName: String,
    val myEvent: Boolean
)

data class Interest(
    val interestsId: Int,
    val name: String
)

data class InterestStatistic(
    val count: Int,
    val name: String
)

data class GenderStatistic(
    val count: Int,
    val key: String
)