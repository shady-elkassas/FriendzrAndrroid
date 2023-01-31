package com.friendzrandroid.home.fragment.home.messages.allFriends

import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.MyFriendsUseCase
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AllFriendsViewModel @Inject constructor(private val myFriendsUseCase: MyFriendsUseCase) :
    PagingViewModel<FeedRequestUserData>() {


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

}