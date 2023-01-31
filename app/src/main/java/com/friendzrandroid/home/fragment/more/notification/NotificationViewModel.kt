package com.friendzrandroid.home.fragment.more.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.data.network.NetworkResponseStatus
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.NotificationData
import com.friendzrandroid.home.domain.interactor.GetNotificationsUseCase
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel@Inject constructor(private val notificationUseCase:GetNotificationsUseCase) : PagingViewModel<NotificationData>() {


    override fun loadData() {
        notificationUseCase.execute(PagingListRequest(pageSize, pageNumber)).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {
                        totalItemCount.value = it.model!!.totalRecords
                        setData(it.model!!.data)

                    } else {
                        if (pageNumber==1){
                            totalItemCount.value = 0
                        }
                    }

                }
            }.launchIn(viewModelScope)
    }

}