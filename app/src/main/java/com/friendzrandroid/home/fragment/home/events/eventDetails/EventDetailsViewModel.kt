package com.friendzrandroid.home.fragment.home.events.eventDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.EventDetails
import com.friendzrandroid.home.data.model.enum.CickUserFromEventEnum
import com.friendzrandroid.home.domain.interactor.events.CickUserFromEventUseCase
import com.friendzrandroid.home.domain.interactor.events.EventDetailsUseCase
import com.friendzrandroid.home.domain.interactor.events.JoinEventUseCase
import com.friendzrandroid.home.domain.interactor.events.LeaveEventUseCase
import com.friendzrandroid.home.domain.model.CickUserFromEventRequest
import com.friendzrandroid.home.domain.model.LeaveEventRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventDetailsUseCase: EventDetailsUseCase,
    private val joinEventUseCase: JoinEventUseCase,
    private val leaveEventUseCase: LeaveEventUseCase,
    private val clickUserFromEventUseCase: CickUserFromEventUseCase
) : BaseViewModel() {

    val eventDetails = MutableLiveData<DataState<EventDetails>>()
    val loading = MutableLiveData<Boolean>()
    val actionToUser = MutableLiveData<Int>()

    fun getEventDetails(id: String) {

        eventDetailsUseCase.execute(id).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response!!.isSuccessful && response.model != null) {
                eventDetails.value = DataState.NewData(response.model)
            } else {
                eventDetails.value = DataState.FailData(response.message)

            }

        }.launchIn(viewModelScope)
    }


    fun joinEvent(eventID: String) {
        loading.value = true
        joinEventUseCase.execute(eventID).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            response?.let {
                if (response.isSuccessful)
                    getEventDetails(eventID)

            }

            loading.postValue(false)

        }.launchIn(viewModelScope)
    }


    fun leaveEvent(eventLeaveRequest: LeaveEventRequest) {
        loading.value = true
        leaveEventUseCase.execute(eventLeaveRequest).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response!!.isSuccessful) {
                getEventDetails(eventLeaveRequest.eventId)
            } else {
                eventDetails.value = DataState.FailData(response.message)
            }

            loading.value = false

        }.launchIn(viewModelScope)
    }

    fun clickUserEvent(eventID: String, userID: String, status: CickUserFromEventEnum) {

        clickUserFromEventUseCase.execute(CickUserFromEventRequest(eventID, userID, status))
            .flowOn(Dispatchers.IO).onEach {

                val response = validateResponse(it)

                if (response!!.isSuccessful) {
                    actionToUser.postValue(status.value)
                    getEventDetails(eventID)
                }


            }.launchIn(viewModelScope)
    }


}