package com.friendzrandroid.home.domain.interactor.userSettingsAndProfile

import android.util.Log
import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.ReportReasonModel
import com.friendzrandroid.home.data.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllReportsReasonsUseCase @Inject constructor(private val reportReasonsRepository: ReportRepository) :
    BaseUseCase<Any, ResultWrapper<BaseDataWrapper<List<ReportReasonModel>>>>() {

    override fun execute(params: Any): Flow<ResultWrapper<BaseDataWrapper<List<ReportReasonModel>>>> {
        return flow {
            val x = reportReasonsRepository.getAllReportsReasons()
            Log.i("Report UseCase", "execute: $x")
            emit(x)
        }
    }
}