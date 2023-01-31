package com.friendzrandroid.home.fragment.home.messages.group.addUser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.domain.interactor.chat.group.AddUserToGroupUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.MyFriendsUseCase
import com.friendzrandroid.home.domain.model.UserWithGroupRequest
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AddUserGroupViewModel @Inject constructor(
    val addUserUseCase: AddUserToGroupUseCase,
    val myFriendsUseCase: MyFriendsUseCase
) : PagingViewModel<FeedRequestUserData>() {

    val addUserToGroupState: MutableLiveData<DataState<Any>> = MutableLiveData()

    override fun loadData() {
        myFriendsUseCase.execute(PagingListRequest(pageSize, pageNumber)).flowOn(Dispatchers.IO)
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


    fun addUserToGroup(data: UserWithGroupRequest) {

        addUserUseCase.execute(data).flowOn(Dispatchers.IO).onEach {
            val response = validateResponse(it)

            response?.let {
                if (it.isSuccessful)
                    addUserToGroupState.postValue(DataState.NewData(it.model!!))
                else
                    addUserToGroupState.postValue(DataState.FailData(it.message))
            }
        }.launchIn(viewModelScope)
    }
}