package com.friendzrandroid.home.fragment.home.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.domain.interactor.events.GetEventsByLocationUseCase
import com.friendzrandroid.home.domain.interactor.tags.GetIAllCategoryUseCase
import com.friendzrandroid.home.domain.interactor.map.GetEventsAroundMeUseCase
import com.friendzrandroid.home.domain.model.MapFilterRequest
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
//    private val eventsUseCase: GetAllLocationEventUseCase,
    private val interestsUseCase: GetIAllCategoryUseCase,
    private val eventsByLocationUseCase: GetEventsByLocationUseCase,
    private val eventAroundMeUseCase: GetEventsAroundMeUseCase
) :
    BaseViewModel() {
    var userLat: Double = 0.0
    var userLong: Double = 0.0
    val isLoading = MutableLiveData<Boolean>()
    val allInterestList = ArrayList<InterestData>()
    var _allLocationEvents =
        MutableLiveData<List<EventMapResponseItem>>()
    val allLocationEvents: LiveData<List<EventMapResponseItem>> = _allLocationEvents



    private val _eventsByLocation =
        MutableLiveData<List<EventMapData>>()
    var _eventsGD =
        MutableLiveData<List<LocationGenderDistribution>>()
    fun getAroundMeData(filterSelectedTags: String) {

        eventAroundMeUseCase.execute(
            MapFilterRequest(
                LatLng(userLat, userLong),
                filterSelectedTags
            )
        ).flowOn(Dispatchers.IO).onEach {
            val response = validateResponse(it)
            response?.let {
                if (it.isSuccessful) {
                    it.model?.let {
                        _allLocationEvents.value = it.eventlocationDataMV
                        _eventsGD.value = it.locationMV

                    }
                } else {
                }
            }
        }.launchIn(viewModelScope)
    }
    fun getEventsByLocation(params: LatLng) {
        eventsByLocationUseCase.execute(params).flowOn(Dispatchers.IO).onEach {
            val response = validateResponse(it)
            response?.let {
                _eventsByLocation.value = it.model!!
            }
        }.launchIn(viewModelScope)
    }

    fun getUserInterests() {
        isLoading.postValue(true)
        interestsUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response!!.isSuccessful) {

                allInterestList.clear()
                allInterestList.addAll(response.model!!)

            }

            isLoading.postValue(false)

        }.launchIn(viewModelScope)
    }


    fun deleteLiveData() {
//        _allLocationEvents.value = null
        _allLocationEvents =
            MutableLiveData<List<EventMapResponseItem>>()

        _eventsGD =
            MutableLiveData<List<LocationGenderDistribution>>()
    }
}