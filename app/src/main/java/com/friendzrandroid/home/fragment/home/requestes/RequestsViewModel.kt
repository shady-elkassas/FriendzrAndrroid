package com.friendzrandroid.home.fragment.home.requestes

import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.data.network.NetworkResponseStatus
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.domain.interactor.requests.ChangeRequestStatusUseCase
import com.friendzrandroid.home.domain.interactor.RequestsUseCase
import com.friendzrandroid.home.domain.model.ChangeFeedStatusRequest
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val requestsUseCase: RequestsUseCase,
    val updateUserStatusUseCase: ChangeRequestStatusUseCase
) : PagingViewModel<FeedRequestUserData>() {

    var currentTab: Int = 2

    override fun loadData() {
        requestsUseCase.execute(PagingListRequest(20, pageNumber, requestType = currentTab))
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {
                        totalItemCount.value = it.model!!.totalRecords

                        //val listToShow = it.model.data.filter { it.key == currentTab }
                        setData(it.model.data)

                    } else if (it.statusCode == NetworkResponseStatus.INTERNAL_SERVER_ERROR.status) {
                        if (pageNumber == 1) {
                            totalItemCount.value = 0
                        }
                    }

                }
            }.launchIn(viewModelScope)
    }


    suspend fun updateUserStatus(userid: String, key: Int): Boolean {

        val data =
            validateResponse(updateUserStatusUseCase.execute(ChangeFeedStatusRequest(userid, key)))

        if (data != null)
            return data.isSuccessful
        return false
    }

}