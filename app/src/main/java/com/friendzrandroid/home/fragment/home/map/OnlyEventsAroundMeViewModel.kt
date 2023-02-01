package com.friendzrandroid.home.fragment.home.map

import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.data.network.NetworkResponseStatus
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.data.model.EventsResponse
import com.friendzrandroid.home.data.model.events.OnlyEventsAroundMeModel
import com.friendzrandroid.home.domain.interactor.map.GetOnlyEventsAroundMeUseCase
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OnlyEventsAroundMeViewModel @Inject constructor(private val onlyEventsAroundMeUseCase: GetOnlyEventsAroundMeUseCase) :
    PagingViewModel<EventItemData>() {
    var degree = 0
    var userLat: Double = 0.0
    var userLang: Double = 0.0
    var filterSelectedTags: String = ""
    var dateCriteria: String? = null
    var startData: String? = null
    var endData: String? = null

    override fun loadData() {

        if (loadMore) {


            onlyEventsAroundMeUseCase.execute(
                PagingListRequest(
                    pageSize,
                    pageNumber,
                    userLat,
                    userLang,
                    filterSelectedTags = filterSelectedTags,
                    dateCriteria = dateCriteria,
                    startDate = startData,
                    endDate = endData
                )
            )
                .flowOn(Dispatchers.IO)
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
                        }

                    }
                }.launchIn(viewModelScope)
        }
    }

}