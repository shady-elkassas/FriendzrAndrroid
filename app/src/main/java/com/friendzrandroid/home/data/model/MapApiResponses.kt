package com.friendzrandroid.home.data.model

import java.io.Serializable


class EventByLocationResponse : ArrayList<EventMapData>()

class EventAllLocationResponse : ArrayList<EventMapResponseItem>()


data class EventAroundMeResponse(
    val eventlocationDataMV:List<EventMapResponseItem>,
    val locationMV:List<LocationGenderDistribution>
)

data class LocationGenderDistribution(
    val color: String,
    val femalepercentage: Double,
    val lang: Double,
    val lat: Double,
    val malePercentage: Double,
    val otherpercentage: Double,
    val totalUsers: Int
):Serializable
data class EventMapData(
    val allday: Boolean,
    val categorieId: Any,
    val categorieimage: String,
    val category: String,
    val description: String,
    val entityId: Any,
    val eventdate: String,
    val eventdateto: String,
    val id: String,
    val image: String,
    val UserImage: String,
    val lang: String,
    val lat: String,
    val status: Any,
    val timefrom: String,
    val timeto: String,
    val joined:Int,
    val title: String,
    val totalnumbert: Int,
    val userId: Int,
    val eventtypeid: String,
    val eventtype: String,
    val eventTypeName: String

    )
data class EventMapResponseItem(
    val color: String?,
    val eventTypeName: String?,
    val event_Type: String?,
    val eventMarkerImage: String?,
    val count: Int,
    val eventData: List<EventMapData>,
    val lang: Double,
    val lat: Double,

)



//data class EventByLocationResponseItem(
//    val allday: Boolean,
//    val attendees: List<Any>,
//    val categorie: String,
//    val categorieimage: String,
//    val description: String,
//    val eventdate: String,
//    val eventdateto: String,
//    val id: String,
//    val image: String,
//    val joined: Int,
//    val key: Int,
//    val lang: String,
//    val lat: String,
//    val timefrom: String,
//    val timeto: String,
//    val title: String,
//    val totalnumbert: Int
//)