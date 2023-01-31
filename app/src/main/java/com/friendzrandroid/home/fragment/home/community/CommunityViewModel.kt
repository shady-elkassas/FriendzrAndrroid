package com.friendzrandroid.home.fragment.home.community

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.data.model.community.RecentlyConnectedItemData
import com.friendzrandroid.home.data.model.community.RecommendedEventResponse
import com.friendzrandroid.home.data.model.community.RecommendedPeopleResponse
import com.friendzrandroid.home.domain.interactor.requests.ChangeRequestStatusUseCase
import com.friendzrandroid.home.domain.interactor.community.GetRecentlyConnectedUseCase
import com.friendzrandroid.home.domain.interactor.community.GetRecommendedEventsUseCase
import com.friendzrandroid.home.domain.interactor.community.GetRecommendedPeopleUseCase
import com.friendzrandroid.home.domain.model.ChangeFeedStatusRequest
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val getRecommendedPeopleUseCase: GetRecommendedPeopleUseCase,
    private val getRecommendedEventsUseCase: GetRecommendedEventsUseCase,
    private val getRecentlyConnectedUseCase: GetRecentlyConnectedUseCase,
    val updateUserStatusUseCase: ChangeRequestStatusUseCase
) :
    PagingViewModel<RecentlyConnectedItemData>() {

    val isErrorFound = MutableLiveData<Boolean>()
    val recommendedConnections =
        MutableLiveData<DataState<RecommendedPeopleResponse>>()

    val recommendedEvents =
        MutableLiveData<DataState<RecommendedEventResponse>>()

    override fun loadData() {
        getRecentlyConnectedUseCase.execute(
            PagingListRequest(
                pageSize,
                pageNumber
            )
        )
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {
                        isErrorFound.value = false

                        totalItemCount.value = it.model!!.totalRecords
                        setData(it.model.data)
                        if (pageNumber == 1 && it.model.data.size == 0) {
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


    fun getRecommendedConnections(userId: String) {

        getRecommendedPeopleUseCase.execute(userId).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response.isSuccessful) {
                    isErrorFound.value = false

                    if (response.model != null) {
                        val model = response.model

                        val userId = response.model.userId

                        recommendedConnections.postValue(DataState.NewData(model))
                    } else {
                        recommendedConnections.postValue(DataState.FailData(response.message))


                    }

                } else {
//                    recommendedConnections.postValue(DataState.FailData(response.message))
                    var x = 0
                    isErrorFound.value = true


                }

            } else {
                var x = 0
                isErrorFound.value = true


            }


        }.launchIn(viewModelScope)

    }

    fun getRecommendedEvents(userId: String) {

        getRecommendedEventsUseCase.execute(userId).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response.isSuccessful) {

                    if (response.model != null) {
                        val model = response.model
                        isErrorFound.value = false

                        val eventId = response.model.eventId
                        recommendedEvents.postValue(DataState.NewData(model))

                    } else {
                        recommendedEvents.postValue(DataState.FailData(response.message))
                    }

                } else {
//                    recommendedEvents.postValue(DataState.FailData(response.message))
                    isErrorFound.value = true

                    var x = 0
                }

            } else {
                var x = 0
                isErrorFound.value = true

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


