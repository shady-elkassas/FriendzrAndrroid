package com.friendzrandroid.home.data.datasource

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.home.data.model.ReportReasonModel
import retrofit2.Response
import retrofit2.http.*

interface ReportAPIS {

    @GET("ReportReason/getAllReportReasons")
    suspend fun getAllReportReasons(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<List<ReportReasonModel>>>


    @POST("Report/sendReport")
    @FormUrlEncoded
    suspend fun sendUserReport(
        @Header("Authorization") authHeader: String,
        @Field("ID") userId: String,
        @Field("isEvent") isEvent: Boolean,
        @Field("Message") reportMessage: String?,
        @Field("ReportReasonID") reportReasonId: String,
        @Field("ReportType") reportType: Int
    ): Response<BaseDataWrapper<Any>>
}