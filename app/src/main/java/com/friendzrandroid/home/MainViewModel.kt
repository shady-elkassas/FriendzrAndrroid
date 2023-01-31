package com.friendzrandroid.home

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.UpdateUserLocationResponse
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.UpdateCurrentLocationUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val updateLocationUseCase: UpdateCurrentLocationUseCase,
    //private val getRequestsUseCase: RequestsUseCase
) : BaseViewModel() {
    val isLocationEnabled = MutableLiveData<Boolean>()
    val currentLocation = MutableLiveData<Location>()

    //val currentRequests: MutableLiveData<Int> = MutableLiveData()
    val currentUserBadges: MutableLiveData<DataState<UpdateUserLocationResponse>> =
        MutableLiveData()

    fun updateLocation(data: LatLng) {
        updateLocationUseCase.execute(data).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            response?.let {
                if (it.isSuccessful)
                    currentUserBadges.postValue(DataState.NewData(it.model!!))
                else
                    currentUserBadges.postValue(DataState.FailData(it.message))
            }

        }.launchIn(viewModelScope)
    }

//    fun getRequests() {
//        getRequestsUseCase.execute(PagingListRequest(20, 1, requestType = 2))
//            .flowOn(Dispatchers.IO)
//            .onEach {
//                val response = validateResponse(it)
//
//                response?.let {
//                    if (it.isSuccessful)
//                        currentRequests.postValue(it.model?.totalRecords)
//                    else
//                        currentRequests.postValue(0)
//                }
//            }.launchIn(viewModelScope)
//    }
}