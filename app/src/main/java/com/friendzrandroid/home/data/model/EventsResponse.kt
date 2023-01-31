package com.friendzrandroid.home.data.model

data class EventsResponse(
    val data: List<EventItemData>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)