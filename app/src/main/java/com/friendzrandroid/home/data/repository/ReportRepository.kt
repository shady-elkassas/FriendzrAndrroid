package com.friendzrandroid.home.data.repository

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.datasource.ReportAPIS
import com.friendzrandroid.home.data.model.ReportReasonModel
import com.friendzrandroid.home.domain.model.SendReportRequest
import javax.inject.Inject

class ReportRepository @Inject constructor(private val api: ReportAPIS) : SafeApiRequest() {

    suspend fun getAllReportsReasons(): ResultWrapper<BaseDataWrapper<List<ReportReasonModel>>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest { api.getAllReportReasons(token!!) } as ResultWrapper<BaseDataWrapper<List<ReportReasonModel>>>
    }

    suspend fun sendReport(reportRequestParam: SendReportRequest): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {
            api.sendUserReport(
                token!!,
                reportRequestParam.userId,
                reportRequestParam.isEvent,
                reportRequestParam.reportMessage,
                reportRequestParam.reportReasonId,
                reportRequestParam.reportType
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }
}