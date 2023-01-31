package com.friendzrandroid.home.fragment.home.events.editEvents

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.EventAttende
import com.friendzrandroid.home.data.model.EventDetails
import com.friendzrandroid.home.data.model.EventTypesResponse
import com.friendzrandroid.home.data.model.enum.CickUserFromEventEnum
import com.friendzrandroid.home.domain.interactor.events.*
import com.friendzrandroid.home.domain.model.CickUserFromEventRequest
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val eventDataUseCase: EventDetailsUseCase,
    private val updateEventDataUseCase: UpdateEventUseCase,
    private val cickUserUseCase: CickUserFromEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val eventTypesUseCase: GetEventTypesUseCase,
    private val getEventAttendanceUseCase: GetEventAttendanceUseCase
) : BaseViewModel() {

    val eventTypes = MutableLiveData<List<EventTypesResponse>>()

    val eventDetails = MutableLiveData<DataState<EventDetails>>()
    val eventAttendanceList = MutableLiveData<DataState<EventAttende>>()

    val isLoading = MutableLiveData<Boolean>()
    val deleteEvent = MutableLiveData<Boolean>()

    val eventUpdated = MutableLiveData<DataState<Any>>()

    val dateFrom = MutableLiveData<Date>(null)
    val dateTo = MutableLiveData<Date>(null)
    val timeFrom = MutableLiveData<Date>(null)
    val timeTo = MutableLiveData<Date>(null)


    fun getEventDetails(id: String) {

        isLoading.postValue(true)

        //eventDetails.value = DataState.Loading()
        eventDataUseCase.execute(id).flowOn(Dispatchers.IO).onEach {

            isLoading.postValue(false)

            val response = validateResponse(it)
            if (response!!.isSuccessful) {
                eventDetails.value = DataState.NewData(response.model!!)
            } else {
                eventDetails.value = DataState.FailData(response.message)

            }

        }.launchIn(viewModelScope)
    }


    fun editEvent(
        eventId: String,
        title: String,
        description: String,
        totalNumber: Int,
        eventDate: String,//date from
        allDay: Boolean,
        eventDateTo: String,// date to
        eventFrom: String,// time from
        eventTo: String,// time to
        eventImage: File?,
        eventTypeId: String,
        listOfUsers: String?,
        showAttendee: Boolean?
    ) {

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap.put("Title", RequestBody.create("text/plain".toMediaTypeOrNull(), title))
        requestBodyMap.put("eventId", RequestBody.create("text/plain".toMediaTypeOrNull(), eventId))

        requestBodyMap.put(
            "description",
            RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        )
        requestBodyMap.put(
            "totalnumbert",
            RequestBody.create("text/plain".toMediaTypeOrNull(), totalNumber.toString())
        )
        requestBodyMap.put(
            "allday",
            RequestBody.create("text/plain".toMediaTypeOrNull(), allDay.toString())
        )
        requestBodyMap.put(
            "eventdate",
            RequestBody.create("text/plain".toMediaTypeOrNull(), eventDate)
        )
        requestBodyMap.put(
            "eventdateto",
            RequestBody.create("text/plain".toMediaTypeOrNull(), eventDateTo)
        )
        requestBodyMap.put(
            "eventfrom",
            RequestBody.create("text/plain".toMediaTypeOrNull(), eventFrom)
        )
        requestBodyMap.put("eventto", RequestBody.create("text/plain".toMediaTypeOrNull(), eventTo))

        requestBodyMap.put(
            "eventtype",
            RequestBody.create("text/plain".toMediaTypeOrNull(), eventTypeId)
        )
        var filePart: MultipartBody.Part? = null
        if (eventImage != null) {
            filePart = MultipartBody.Part.createFormData(
                "Eventimage",
                URLEncoder.encode(eventImage.getName(), "utf-8"),
                RequestBody.create("image/*".toMediaTypeOrNull(), eventImage)
            )

        }

        if (listOfUsers != null && showAttendee != null) {
            requestBodyMap.put(
                "ListOfUserIDs",
                RequestBody.create("text/plain".toMediaTypeOrNull(), listOfUsers)
            )
            requestBodyMap.put(
                "showAttendees",
                RequestBody.create("text/plain".toMediaTypeOrNull(), showAttendee.toString())
            )
        }

        //isLoading.postValue(true)
        updateEventDataUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
            .flowOn(Dispatchers.IO).onEach {

                val response = validateResponse(it)
                if (response!!.isSuccessful) {
                    eventUpdated.postValue(DataState.NewData(Any()))
                } else {
                    eventUpdated.postValue(DataState.FailData(response.message))

                }

                //isLoading.postValue(false)

            }.launchIn(viewModelScope)
    }


    fun cickUser(eventID: String, userID: String, status: CickUserFromEventEnum) {

        //isLoading.postValue(true)
        cickUserUseCase.execute(CickUserFromEventRequest(eventID, userID, status))
            .flowOn(Dispatchers.IO).onEach {

                val response = validateResponse(it)

                if (response!!.isSuccessful) {
                    getEventDetails(eventID)
                }

                //isLoading.postValue(false)

            }.launchIn(viewModelScope)
    }


    fun deleteEvent(eventID: String) {

        //isLoading.postValue(true)
        deleteEventUseCase.execute(eventID)
            .flowOn(Dispatchers.IO).onEach {

                val response = validateResponse(it)

                if (response!!.isSuccessful) {
                    deleteEvent.postValue(true)
                }

                //isLoading.postValue(false)

            }.launchIn(viewModelScope)
    }

    fun getEventTypes() {
        eventTypesUseCase.execute(Any()).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)

                if (response!!.isSuccessful)
                    eventTypes.postValue(response.model!!)

            }.launchIn(viewModelScope)
    }

    fun getEventAttendee(id: String) {
        getEventAttendanceUseCase.execute(id)
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (response.isSuccessful) {
                        if (response.model != null) {
                            eventAttendanceList.value = DataState.NewData(response.model)

                        }

                    } else {
                        eventAttendanceList.value = DataState.FailData(response.message)
                    }
                }
            }.launchIn(viewModelScope)
    }
}