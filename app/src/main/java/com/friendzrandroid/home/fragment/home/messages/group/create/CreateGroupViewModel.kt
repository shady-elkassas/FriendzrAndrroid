package com.friendzrandroid.home.fragment.home.messages.group.create

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.groupchat.GroupChatResponse
import com.friendzrandroid.home.domain.interactor.chat.group.CreateGroupChatUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.MyFriendsUseCase
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.friendzrandroid.home.domain.model.PagingListRequest
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
class CreateGroupViewModel @Inject constructor(
    val myFriendsUseCase: MyFriendsUseCase,
    val createGroupChatUseCase: CreateGroupChatUseCase
) : PagingViewModel<FeedRequestUserData>() {

    private val TAG = "CreateGroupViewModel"

    val isGroupCreated: MutableLiveData<DataState<GroupChatResponse>> = MutableLiveData()

    override fun loadData() {
        myFriendsUseCase.execute(PagingListRequest(pageSize, pageNumber, search = searchText)).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {
                        totalItemCount.value = it.model!!.totalRecords
                        setData(it.model!!.data)

                    } else {
                        if (pageNumber == 1) {
                            totalItemCount.value = 0
                        }
                    }

                }
            }.launchIn(viewModelScope)
    }

    fun createGroupChat(
        groupName: String,
        groupCreationDate: String,
        groupListOfIds: String,
        groupImage: File?
    ) {

        Log.e(
            TAG,
            "createGroupChat: Name: $groupName, Creation: $groupCreationDate, Ids: $groupListOfIds, image: $groupImage",
        )

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap.put("Name", RequestBody.create("text/plain".toMediaTypeOrNull(), groupName))
        requestBodyMap.put(
            "ListOfUserIDs",
            RequestBody.create("text/plain".toMediaTypeOrNull(), groupListOfIds)
        )
        requestBodyMap.put(
            "RegistrationDateTime",
            RequestBody.create("text/plain".toMediaTypeOrNull(), groupCreationDate)
        )

        var filePart: MultipartBody.Part? = null
        if (groupImage != null) {
            filePart = MultipartBody.Part.createFormData(
                "Image_File",
                groupImage.getName(),
                RequestBody.create("image/*".toMediaTypeOrNull(), groupImage)
            )

        }

        createGroupChatUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
            .flowOn(Dispatchers.IO)
            .onEach {


                val response = validateResponse(it)

                Log.e(TAG, "createGroupChat: $it")
                Log.e(TAG, "createGroupChat: $response")

                response?.let {
                    if (it.isSuccessful)
                        isGroupCreated.postValue(DataState.NewData(it.model!!))
                    else
                        isGroupCreated.postValue(DataState.FailData(it.message))
                }

            }.launchIn(viewModelScope)
    }
}