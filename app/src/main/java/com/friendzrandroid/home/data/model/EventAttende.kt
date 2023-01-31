package com.friendzrandroid.home.data.model

data class EventAttende(
    val data: List<AttendenceData>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)
