package com.friendzrandroid.home.fragment.home.messages.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.data.network.NetworkResponseStatus
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.core.utils.FormatToDate
import com.friendzrandroid.core.utils.FormatToTime
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.data.model.enum.MessageTypeEnum
import com.friendzrandroid.home.domain.SendUserMessageUseCase
import com.friendzrandroid.home.domain.interactor.chat.*
import com.friendzrandroid.home.domain.interactor.chat.group.GetGroupChatInboxUserCase
import com.friendzrandroid.home.domain.interactor.chat.group.SendGroupMessageUseCase
import com.friendzrandroid.home.domain.interactor.requests.ChangeRequestStatusUseCase
import com.friendzrandroid.home.domain.model.ChangeFeedStatusRequest
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.friendzrandroid.home.domain.model.PagingMessageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder
import java.util.*
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val eventChatUseCase: GetEventChatDataUseCase,
    private val userChatUseCase: GetUserChatDataUseCase,
    private val groupChatUseCase: GetGroupChatInboxUserCase,
    private val sendEventUseCase: SendEventMessageUseCase,
    private val sendUserUseCase: SendUserMessageUseCase,
    private val sendGroupUseCase: SendGroupMessageUseCase,
    private val changeStateUseCase: ChangeRequestStatusUseCase
) :
    PagingViewModel<MessageData>() {
    var chatID: String = ""
    var isEvent: Boolean = true
    var isFriend: Boolean = true
    var isChatGroup: Boolean = true
    private var resetData = false

    val updateUserState = MutableLiveData<DataState<Boolean>>()

    override fun loadData() {
        if (isEvent) {
            eventChatUseCase.execute(PagingMessageRequest(pageSize, pageNumber, chatID))
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = validateResponse(it)
                    response?.let {
                        if (it.isSuccessful) {
                            totalItemCount.postValue(it.model!!.pagedModel.data.size)
                            if (resetData) {
                                resetData(it.model!!.pagedModel.data)
                                resetData = false
                            } else
                                setData(it.model!!.pagedModel.data)

                        } else if (it.statusCode == NetworkResponseStatus.INTERNAL_SERVER_ERROR.status) {
                            if (pageNumber == 1) {
                                totalItemCount.postValue(0)
                            }
                        }

                    }
                }.launchIn(viewModelScope)

        } else if (isChatGroup) {
            groupChatUseCase.execute(PagingMessageRequest(pageSize, pageNumber, chatID))
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = validateResponse(it)
                    response?.let {
                        if (it.isSuccessful) {

                            totalItemCount.postValue(it.model!!.pagedModel.data.size)
                            if (resetData) {
                                resetData(it.model.pagedModel.data)
                                resetData = false
                            } else {
                                setData(it.model.pagedModel.data)
                            }

                        } else {
                            totalItemCount.value = 0
                        }

                    }
                }.launchIn(viewModelScope)

        } else {

            userChatUseCase.execute(PagingMessageRequest(pageSize, pageNumber, chatID))
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = validateResponse(it)
                    response?.let {
                        if (it.isSuccessful) {
                            totalItemCount.postValue(it.model!!.data.size)
                            if (resetData) {
                                resetData(it.model!!.data)
                                resetData = false
                            } else
                                setData(it.model!!.data)


                        } else {
                            totalItemCount.value = 0
                        }

                    }
                }.launchIn(viewModelScope)

        }
    }

    fun sendMessage(message: String, messageType: MessageTypeEnum, file: File?) {
        val msgType = messageType.value.toString()
        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap.put("Message", RequestBody.create("text/plain".toMediaTypeOrNull(), message))
        Log.d("chat", "message: is: $message")

        requestBodyMap.put(
            "Messagetype",
            RequestBody.create("text/plain".toMediaTypeOrNull(), messageType.value.toString())
        )
        Log.d("chat", "messageType: is: $msgType")

        val currentDate = Date()
        val mesgDate = currentDate.FormatToDate()
        val mesgTime = currentDate.FormatToTime()

        var filePart: MultipartBody.Part? = null

        if (file != null && messageType.value == MessageTypeEnum.IMAGE.value) {
            if (isChatGroup) {
                filePart = MultipartBody.Part.createFormData(
                    "Attach_File",
                    URLEncoder.encode(file.getName(), "utf-8"),
                    RequestBody.create("image/*".toMediaTypeOrNull(), file)
                )
            } else {
                filePart = MultipartBody.Part.createFormData(
                    "Attach",
                    URLEncoder.encode(file.getName(), "utf-8"),
                    RequestBody.create("image/*".toMediaTypeOrNull(), file)
                )
                Log.d("chat", "image: is: $file")

            }

        } else if (file != null && messageType.value == MessageTypeEnum.FILE.value) {

            if (isChatGroup) {
                Log.d("chat", "sendMessage: File: $file")
                filePart = MultipartBody.Part.createFormData(
                    "Attach_File",
                    URLEncoder.encode(file.name, "utf-8"),
                    RequestBody.create("*/*".toMediaTypeOrNull(), file)


                )
                Log.d("chat", "file: is: $file")
            } else {


                Log.d("chat", "sendMessage: File: $file")
                filePart = MultipartBody.Part.createFormData(
                    "Attach",
                    URLEncoder.encode(file.name, "utf-8"),
                    RequestBody.create("*/*".toMediaTypeOrNull(), file)


                )
                Log.d("chat", "file: is: $file")
            }

        }
        resetData = true

        Log.d("TAG", "sendMessage: isEvent: $isEvent, isFriend: $isFriend, isGroup: $isChatGroup")

        if (isEvent) {


            requestBodyMap.put(
                "messagesdate",
                RequestBody.create("text/plain".toMediaTypeOrNull(), mesgDate)
            )
            Log.d("chat", "date: is: $mesgDate")

            requestBodyMap.put(
                "messagestime",
                RequestBody.create("text/plain".toMediaTypeOrNull(), mesgTime)
            )
            Log.d("chat", "time: is: $mesgTime")

            requestBodyMap.put("EventId", RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                chatID
            ))

            sendEventUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = validateResponse(it)
                    response?.let {
                        if (it.isSuccessful) {
                            pageNumber = 1
                            //loadData()
                        }
                    }
                }.launchIn(viewModelScope)

        } else if (isFriend) {
            requestBodyMap.put(
                "messagesdate",
                RequestBody.create("text/plain".toMediaTypeOrNull(), mesgDate)
            )
            Log.d("chat", "date: is: $mesgDate")

            requestBodyMap.put(
                "messagestime",
                RequestBody.create("text/plain".toMediaTypeOrNull(), mesgTime)
            )
            Log.d("chat", "time: is: $mesgTime")

            requestBodyMap.put("UserId", RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                chatID
            ))

            Log.d("chat", "requestBodyMap: is: $requestBodyMap")

            sendUserUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = validateResponse(it)
                    response?.let {
                        if (it.isSuccessful) {
                            pageNumber = 1
                            //loadData()
                        }
                    }
                }.launchIn(viewModelScope)

        } else if (isChatGroup) {

            val dateTime = mesgDate +" "+ mesgTime
            val dateFormate = currentDate.toString()

            requestBodyMap.put(
                "MessagesDateTime",
                RequestBody.create("text/plain".toMediaTypeOrNull(), dateTime)
            )

            Log.d("chat", "date: is: $dateTime")
            Log.d("chat", "dateFormate: is: $dateFormate")



            requestBodyMap.put(
                "ChatGroupID",
                RequestBody.create("text/plain".toMediaTypeOrNull(), chatID)
            )


            sendGroupUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = validateResponse(it)
                    response?.let {
                        if (it.isSuccessful) {
                            pageNumber = 1
                            //loadData()
                        }
                    }
                }.launchIn(viewModelScope)


        } else {

//            var filePart: MultipartBody.Part? = null
//            if (file != null) {
//                filePart = MultipartBody.Part.createFormData(
//                    "Attach",
//                    file.getName(),
//                    RequestBody.create(MediaType.parse("image/*"), file)
//                )
//
//            }


//            requestBodyMap.put(
//                "MessagesDateTime",
//                RequestBody.create(MediaType.parse("text/plain"), "$mesgDate $mesgTime")
//            )


//            requestBodyMap.put("ChatGroupID", RequestBody.create(MediaType.parse("text/plain"), chatID))
//
//            sendGroupUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
//                .flowOn(Dispatchers.IO)
//                .onEach {
//                    val response = validateResponse(it)
//                    response?.let {
//                        if (it.isSuccessful) {
//                            pageNumber = 1
//                            //loadData()
//                        }
//                    }
//                }.launchIn(viewModelScope)

        }
    }

    fun changeUserState(userID: String, newState: Int) {
        updateUserState.postValue(DataState.Loading(null))
        viewModelScope.launch {

            val response = validateResponse(
                changeStateUseCase.execute(
                    ChangeFeedStatusRequest(
                        userID,
                        newState
                    )
                )
            )

            response?.let {
                if (it.isSuccessful)
                    updateUserState.postValue(DataState.NewData(it.isSuccessful))
                else
                    updateUserState.postValue(DataState.FailData(it.message))
            }
        }
    }

}
