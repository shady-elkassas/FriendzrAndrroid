package com.friendzrandroid.home.fragment.home.events.addEvent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.FormatToDate
import com.friendzrandroid.core.utils.FormatToTime
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.EventTypesResponse
import com.friendzrandroid.home.data.model.InterestData
import com.friendzrandroid.home.domain.interactor.events.GetEventTypesUseCase
import com.friendzrandroid.home.domain.interactor.tags.GetIAllCategoryUseCase
import com.friendzrandroid.home.domain.interactor.map.AddEventUseCase
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
class AddEventViewModel @Inject constructor(
    private val interestsUseCase: GetIAllCategoryUseCase,
    private val addEventUseCase: AddEventUseCase,
    private val eventTypesUseCase: GetEventTypesUseCase
) : BaseViewModel() {

    private val TAG = "AddEventViewModel"

    val isLoading = MutableLiveData<Boolean>()
    val allInterestList = ArrayList<InterestData>()

    val eventAdded = MutableLiveData<DataState<Any>>()

    val eventTypes = ArrayList<EventTypesResponse>()

    val dateFrom = MutableLiveData<Date>(null)
    val dateTo = MutableLiveData<Date>(null)
    val timeFrom = MutableLiveData<Date>(null)
    val timeTo = MutableLiveData<Date>(null)


    fun getUserInterests() {
        isLoading.postValue(true)
        interestsUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response!!.isSuccessful) {

                allInterestList.clear()
                allInterestList.addAll(response.model!!)

            }

            isLoading.postValue(false)

        }.launchIn(viewModelScope)
    }


    fun addEvent(
        title: String,
        description: String,
        categoryID: String,
        eventTypeId: String,
        lat: Double,
        lang: Double,
        totalNumber: Int,
        eventDate: String,//date from
        allDay: Boolean,
        eventDateTo: String,// date to
        eventFrom: String,// time from
        eventTo: String,// time to
        eventImage: File?,
        listOfUsers: String?,
        showAttendee: Boolean?
    ) {

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap.put("Title", RequestBody.create("text/plain".toMediaTypeOrNull(), title))
        requestBodyMap.put(
            "description",
            RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        )
        requestBodyMap.put(
            "categoryid",
            RequestBody.create("text/plain".toMediaTypeOrNull(), categoryID)
        )
        requestBodyMap.put("lat", RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            lat.toString()
        ))
        requestBodyMap.put(
            "lang",
            RequestBody.create("text/plain".toMediaTypeOrNull(), lang.toString())
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

        val date = Date().FormatToDate()
        val time = Date().FormatToTime()

        requestBodyMap.put("CreatDate", RequestBody.create("text/plain".toMediaTypeOrNull(), date))
        requestBodyMap.put("Creattime", RequestBody.create("text/plain".toMediaTypeOrNull(), time))

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

        isLoading.postValue(true)
        addEventUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
            .flowOn(Dispatchers.IO).onEach {
                val response = validateResponse(it)
                if (response!!.isSuccessful) {
                    eventAdded.postValue(DataState.NewData(Any()))
                } else {
                    eventAdded.postValue(DataState.FailData(response.message))

                }

                isLoading.postValue(false)

            }.launchIn(viewModelScope)
    }

    fun getEventTypes() {
        eventTypesUseCase.execute(Any()).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)

                if (response!!.isSuccessful) {
                    eventTypes.clear()
                    eventTypes.addAll(response.model!!)
                }


            }.launchIn(viewModelScope)
    }
}