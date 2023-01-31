package com.friendzrandroid.home.data.model

data class NotificationsResponse(
    val data: List<NotificationData>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)



data class NotificationData(
    val action: String,
    val action_code: String,
    val body: String,
    val createdAt: String,
    val id: String,
    val imageUrl: String,
    val messagetype: Any,
    val muit: Boolean,
    val title: String
)