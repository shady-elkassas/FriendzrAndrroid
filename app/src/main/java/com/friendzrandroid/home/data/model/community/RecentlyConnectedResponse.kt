package com.friendzrandroid.home.data.model.community

data class RecentlyConnectedResponse(
    val data: List<RecentlyConnectedItemData>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)