package com.friendzrandroid.home.fragment.home.messages.inbox

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.InboxData
import com.friendzrandroid.home.domain.interactor.chat.*
import com.friendzrandroid.home.domain.interactor.chat.group.ClearGroupChatUseCase
import com.friendzrandroid.home.domain.interactor.chat.group.LeaveGroupChatUseCase
import com.friendzrandroid.home.domain.interactor.chat.group.MuteGroupChatUseCase
import com.friendzrandroid.home.domain.interactor.chat.group.RemoveGroupChatUseCase
import com.friendzrandroid.home.domain.model.ChatOptionRequest
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val ChatsUseCase: GetUsersInChatUseCase,
    private val searchUseCase: SearchInChatUseCase,
    private val muteChatUseCase: MuteChatUseCase,
    private val muteGroupChatUseCase: MuteGroupChatUseCase,
    private val removeGroupChatUseCase: RemoveGroupChatUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val leaveEventChatUseCase: LeaveEventChatUseCase,
    private val leaveGroupChatUseCase: LeaveGroupChatUseCase,
    private val clearGroupChatUseCase: ClearGroupChatUseCase
) :
    PagingViewModel<InboxData>() {

    val chatOptionState: MutableLiveData<DataState<String>> = MutableLiveData()

    override fun loadData() {
        ChatsUseCase.execute(PagingListRequest(pageSize, pageNumber, search = searchText))
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {
                        totalItemCount.value = it.model!!.totalRecords
                        setData(it.model!!.data)
                        if (pageNumber == 1 && it.model!!.data.size == 0) {
                            isEmptyList.postValue(true)
                        } else {
                            isEmptyList.postValue(false)
                        }
                    } else {
                        if (pageNumber == 1) {
                            totalItemCount.value = 0
                        }
                    }

                }
            }.launchIn(viewModelScope)

    }


//    fun search(txt: String) {
//        searchUseCase.execute(txt).flowOn(Dispatchers.IO)
//            .onEach {
//                val response = validateResponse(it)
//                response?.let {
//                    if (it.isSuccessful) {
//                        singleSetData(it.model!!.data)
//                        if (it.model!!.data.size == 0) {
//                            isEmptyList.postValue(true)
//                        } else {
//                            isEmptyList.postValue(false)
//                        }
//                    }
//                }
//            }.launchIn(viewModelScope)
//
//
//    }

    fun muteChat(
        id: String,
        mute: Boolean,
        isEvent: Boolean
    ) {

        Log.e("TAG", "muteChat: $mute, $isEvent")

        muteChatUseCase.execute(ChatOptionRequest(id, mute, isEvent))
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful)
                        reload()
                    //chatOptionState.postValue(true)
                    //else
                    //chatOptionState.postValue(false)
                }
            }.launchIn(viewModelScope)
    }

    fun muteGroupChat(
        id: String,
        mute: Boolean
    ) {


        muteGroupChatUseCase.execute(ChatOptionRequest(id, mute))
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful)
                        reload()

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
                    if (it.isSuccessful)
                        reload()

                }
            }.launchIn(viewModelScope)
    }


    fun deleteChat(
        id: String,
        isEvent: Boolean
    ) {

        val currentDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val date = "$currentDate $currentTime"

        deleteChatUseCase.execute(
            ChatOptionRequest(
                id = id,
                DeleteDateTime = date,
                isEvent = isEvent
            )
        ).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful)
                        chatOptionState.postValue(DataState.NewData(it.message))
                    else
                        chatOptionState.postValue(DataState.FailData(it.message))
//                    if (it.isSuccessful)
//                        reload()
                    //chatOptionState.postValue(true)
                    //else
                    //chatOptionState.postValue(false)
                }
            }.launchIn(viewModelScope)
    }


    fun leaveEventChat(id: String) {
        Log.e("chat", "leaveEventChat: $id")
        leaveEventChatUseCase.execute(id)
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful)
                        chatOptionState.postValue(DataState.NewData(it.message))
                    else
                        chatOptionState.postValue(DataState.FailData(it.message))
                    //reload()
                }
            }.launchIn(viewModelScope)
    }


    fun leaveGroupChat(id: String) {
        leaveGroupChatUseCase.execute(id)
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful)
                        chatOptionState.postValue(DataState.NewData(it.message))
                    else
                        chatOptionState.postValue(DataState.FailData(it.message))
                }
            }.launchIn(viewModelScope)
    }

    fun clearGroupChat(id: String) {
        clearGroupChatUseCase.execute(id)
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {

                        chatOptionState.postValue(DataState.NewData(it.message))

                    } else {
                        chatOptionState.postValue(DataState.FailData(it.message))
                    }
                }
            }.launchIn(viewModelScope)
    }


}