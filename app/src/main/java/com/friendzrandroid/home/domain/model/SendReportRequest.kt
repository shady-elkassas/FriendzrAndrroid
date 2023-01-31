package com.friendzrandroid.home.domain.model

data class SendReportRequest(
    val userId: String,
    val isEvent: Boolean,
    val reportMessage: String?,
    val reportReasonId: String,
    val reportType: Int
)
