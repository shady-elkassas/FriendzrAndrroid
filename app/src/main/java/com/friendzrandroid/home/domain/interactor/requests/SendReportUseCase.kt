package com.friendzrandroid.home.domain.interactor.requests

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.repository.ReportRepository
import com.friendzrandroid.home.domain.model.SendReportRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendReportUseCase @Inject constructor(private val reportRepository: ReportRepository) :
    BaseUseCase<SendReportRequest, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: SendReportRequest): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(reportRepository.sendReport(params))
        }
    }
}