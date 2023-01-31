package com.friendzrandroid.home.fragment.home.events.eventList

import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.data.network.NetworkResponseStatus
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.domain.interactor.events.GetMyEventsUseCase
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(private val eventsUseCase: GetMyEventsUseCase) :
    PagingViewModel<EventItemData>() {


    override fun loadData() {

        if (loadMore) {
            eventsUseCase.execute(PagingListRequest(pageSize, pageNumber)).flowOn(Dispatchers.IO)
                .onEach {
                    val response = validateResponse(it)
                    response?.let {
                        if (it.isSuccessful) {

                            totalItemCount.value = it.model!!.totalRecords
                            setData(it.model!!.data)

                        } else if (it.statusCode == NetworkResponseStatus.INTERNAL_SERVER_ERROR.status) {
                            if (pageNumber == 1) {
                                totalItemCount.value = 0
                            }

                        }else if (it.statusCode == NetworkResponseStatus.INTERNAL_SERVER_ERROR_2.status) {
                            if (pageNumber == 1) {
                                totalItemCount.value = 0
                            }

                        } else {
                            totalItemCount.value = 0

                        }

                    }
                }.launchIn(viewModelScope)
        }
    }

}