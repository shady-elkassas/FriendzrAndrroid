package com.friendzrandroid.home.fragment.home.events.eventAttendances

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.EventAttende
import com.friendzrandroid.home.data.model.enum.CickUserFromEventEnum
import com.friendzrandroid.home.domain.interactor.events.CickUserFromEventUseCase
import com.friendzrandroid.home.domain.interactor.events.GetEventAttendanceUseCase
import com.friendzrandroid.home.domain.model.CickUserFromEventRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventAttendanceViewModel @Inject constructor(
    private val attendaceUseCase: GetEventAttendanceUseCase,
    private val cickUserUseCase: CickUserFromEventUseCase
) :
    BaseViewModel() {

    private val TAG = "EventAttendanceViewMode"

    val eventAttendanceList = MutableLiveData<DataState<EventAttende>>()
    val acctionHappend = MutableLiveData<Int>()

    fun getEventDetails(id: String) {

        attendaceUseCase.execute(id).flowOn(Dispatchers.IO).onEach {

            eventAttendanceList.value = DataState.Loading(null)
            val response = validateResponse(it)
            if (response!!.isSuccessful) {
                eventAttendanceList.value = DataState.NewData(response.model!!)
            } else {
                eventAttendanceList.value = DataState.FailData(response.message)
            }

        }.launchIn(viewModelScope)
    }


    fun cickUser(eventID: String, userID: String, status: CickUserFromEventEnum) {

        cickUserUseCase.execute(CickUserFromEventRequest(eventID, userID, status))
            .flowOn(Dispatchers.IO).onEach {

                val response = validateResponse(it)

                if (response!!.isSuccessful) {
                    acctionHappend.postValue(status.value)
                    getEventDetails(eventID)
                }


            }.launchIn(viewModelScope)
    }
}