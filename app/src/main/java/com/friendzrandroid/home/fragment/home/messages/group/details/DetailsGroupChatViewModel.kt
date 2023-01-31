package com.friendzrandroid.home.fragment.home.messages.group.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.groupchat.GroupChatResponse
import com.friendzrandroid.home.domain.interactor.chat.GetChatDetailsUseCase
import com.friendzrandroid.home.domain.interactor.chat.group.LeaveGroupChatUseCase
import com.friendzrandroid.home.domain.interactor.chat.group.RemoveGroupChatUseCase
import com.friendzrandroid.home.domain.interactor.chat.group.RemoveUserGroupChatUseCase
import com.friendzrandroid.home.domain.interactor.chat.group.UpdateGroupChatUseCase
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.friendzrandroid.home.domain.model.UserWithGroupRequest
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
import javax.inject.Inject

@HiltViewModel
class DetailsGroupChatViewModel @Inject constructor(
    val detailsChatUseCase: GetChatDetailsUseCase,
    val updateChatUseCase: UpdateGroupChatUseCase,
    private val leaveGroupChatUseCase: LeaveGroupChatUseCase,

    private val removeGroupChatUseCase: RemoveGroupChatUseCase,

    val removeUserGroupChatUseCase: RemoveUserGroupChatUseCase
) : BaseViewModel() {

    val chatDetailsData: MutableLiveData<DataState<GroupChatResponse>> =
        MutableLiveData(DataState.Loading(null))

    val updateChatState: MutableLiveData<DataState<Any>> = MutableLiveData()

    val removeUserState: MutableLiveData<DataState<Any>> = MutableLiveData()

    fun getChatDetails(chatId: String) {
        detailsChatUseCase.execute(chatId)
            .flowOn(Dispatchers.IO)
            .onEach {

                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful)
                        chatDetailsData.postValue(DataState.NewData(it.model!!))
                    else
                        chatDetailsData.postValue(DataState.FailData(it.message))
                }

            }.launchIn(viewModelScope)
    }


    fun updateChat(
        id: String,
        name: String,
        image: File?
    ) {

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap.put("ID", RequestBody.create("text/plain".toMediaTypeOrNull(), id))
        requestBodyMap.put("Name", RequestBody.create("text/plain".toMediaTypeOrNull(), name))

        var filePart: MultipartBody.Part? = null
        if (image != null) {
            filePart = MultipartBody.Part.createFormData(
                "UserImags",
                image.getName(),
                RequestBody.create("image/*".toMediaTypeOrNull(), image)
            )
        }

        updateChatUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)

                response?.let {
                    if (it.isSuccessful)
                        updateChatState.postValue(DataState.NewData(it))
                    else
                        updateChatState.postValue(DataState.FailData(it.message))
                }
            }.launchIn(viewModelScope)
    }


    fun removeUserFromChat(id: String, userId: String, date: String) {
        removeUserGroupChatUseCase.execute(UserWithGroupRequest(id, date, userId))
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)

                response?.let {
                    if (it.isSuccessful)
                        removeUserState.postValue(DataState.NewData(it))
                    else
                        removeUserState.postValue(DataState.FailData(it.message))
                }
            }.launchIn(viewModelScope)
    }


    fun removeGroupChat(
        id: String
    ) {

        removeGroupChatUseCase.execute(id)
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {

                    }

                }
            }.launchIn(viewModelScope)
    }


    fun leaveGroupChat(id: String) {
        leaveGroupChatUseCase.execute(id)
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
//                    if (it.isSuccessful)
//                        chatOptionState.postValue(DataState.NewData(it.message))
//                    else
//                        chatOptionState.postValue(DataState.FailData(it.message))
                }
            }.launchIn(viewModelScope)
    }
}