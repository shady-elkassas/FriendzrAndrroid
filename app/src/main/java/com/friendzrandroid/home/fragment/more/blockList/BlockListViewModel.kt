package com.friendzrandroid.home.fragment.more.blockList

import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.domain.interactor.requests.ChangeRequestStatusUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.GetAllBlockedUseCase
import com.friendzrandroid.home.domain.model.ChangeFeedStatusRequest
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BlockListViewModel @Inject constructor(
    private val blockedListUseCase: GetAllBlockedUseCase,
    val updateUserStatusUseCase: ChangeRequestStatusUseCase,
) :
    PagingViewModel<FeedRequestUserData>() {

    override fun loadData() {
        blockedListUseCase.execute(PagingListRequest(pageSize, pageNumber)).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)

                response?.let {
                    if (it.isSuccessful) {
                        totalItemCount.value = it.model!!.totalRecords
                        setData(it.model?.data)

                    } else {
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