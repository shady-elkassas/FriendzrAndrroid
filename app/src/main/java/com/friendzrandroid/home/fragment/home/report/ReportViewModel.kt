package com.friendzrandroid.home.fragment.home.report

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.ReportReasonModel
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.GetAllReportsReasonsUseCase
import com.friendzrandroid.home.domain.interactor.requests.SendReportUseCase
import com.friendzrandroid.home.domain.model.SendReportRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val allReportsReasonsUseCase: GetAllReportsReasonsUseCase,
    private val sendReportUseCase: SendReportUseCase
) : BaseViewModel() {

    val reportReasons =
        MutableLiveData<DataState<List<ReportReasonModel>>>()

    val reportSendState =
        MutableLiveData<DataState<Any>>()

    fun getAllReportReasons() {

        reportReasons.postValue(DataState.Loading(null))

        allReportsReasonsUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            response?.let {
                if (it.isSuccessful)
                    reportReasons.postValue(DataState.NewData(it.model!!))
                else
                    reportReasons.postValue(DataState.FailData(it.message))
            }
        }.launchIn(viewModelScope)
    }

    fun sendReport(reportRequest: SendReportRequest) {

        sendReportUseCase.execute(reportRequest).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            response?.let {
                if (it.isSuccessful)
                    reportSendState.postValue(DataState.NewData(it))
                else
                    reportSendState.postValue(DataState.FailData(it.message))
            }

        }.launchIn(viewModelScope)
    }
}