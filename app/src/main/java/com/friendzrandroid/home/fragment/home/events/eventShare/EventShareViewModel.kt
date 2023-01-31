package com.friendzrandroid.home.fragment.home.events.eventShare

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.data.network.NetworkResponseStatus
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.core.utils.FormatToDate
import com.friendzrandroid.core.utils.FormatToTime
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.domain.interactor.events.GetMyEventsUseCase
import com.friendzrandroid.home.domain.interactor.chat.SendEventMessageUseCase
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EventShareViewModel @Inject constructor(
    private val getMyEventsUseCase: GetMyEventsUseCase,
    private val sendMessageToEvent: SendEventMessageUseCase
) : PagingViewModel<EventItemData>() {

    private val TAG = "EventShareViewModel"

    val messageSendState: MutableLiveData<DataState<Boolean>> = MutableLiveData()

    override fun loadData() {
        if (loadMore) {
            getMyEventsUseCase.execute(PagingListRequest(pageSize, pageNumber, search = searchText))
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = validateResponse(it)

                    Log.e(TAG, "loadData: response: $response")

                    response?.let {
                        if (it.isSuccessful) {

                            totalItemCount.value = it.model!!.totalRecords
                            setData(it.model!!.data)

                        } else if (it.statusCode == NetworkResponseStatus.INTERNAL_SERVER_ERROR.status) {
                            if (pageNumber == 1) {
                                totalItemCount.value = 0
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        }
    }


    fun shareToEvent(
        selectedId: String,
        message: String,
        messageType: Int,
        idToShareTo: String,
    ) {

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap.put("Message", RequestBody.create("text/plain".toMediaTypeOrNull(), message))
        requestBodyMap.put(
            "Messagetype",
            RequestBody.create("text/plain".toMediaTypeOrNull(), messageType.toString())
        )

        val currentDate = Date()
        val mesgDate = currentDate.FormatToDate()
        val mesgTime = currentDate.FormatToTime()

        requestBodyMap.put(
            "messagesdate",
            RequestBody.create("text/plain".toMediaTypeOrNull(), mesgDate)
        )
        requestBodyMap.put(
            "messagestime",
            RequestBody.create("text/plain".toMediaTypeOrNull(), mesgTime)
        )

        requestBodyMap.put(
            "EventId",
            RequestBody.create("text/plain".toMediaTypeOrNull(), idToShareTo)
        )

        requestBodyMap.put(
            "EventLINKid",
            RequestBody.create("text/plain".toMediaTypeOrNull(), selectedId)
        )


        sendMessageToEvent.execute(FormDataRequestWithImage(requestBodyMap, null))
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)

                response?.let {
                    if (it.isSuccessful) {
                        Log.e(TAG, "shareToEvent: Sucess")
                        messageSendState.postValue(DataState.NewData(true))
                    } else {
                        Log.e(TAG, "shareToEvent: Fail")
                        messageSendState.postValue(DataState.FailData(it.message))
                    }
                }
            }.launchIn(viewModelScope)
    }

}