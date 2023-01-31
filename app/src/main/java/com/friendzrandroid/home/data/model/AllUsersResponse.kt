package com.friendzrandroid.home.data.model

data class AllUsersResponse(
    val data: List<FeedRequestUserData>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)