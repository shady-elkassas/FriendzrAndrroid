package com.friendzrandroid.home.domain.interactor.events

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventByLocationResponse
import com.friendzrandroid.home.data.repository.EventRepository
import com.friendzrandroid.home.data.repository.MapRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventsByLocationUseCase @Inject constructor(private val eventRepository: MapRepository) :
    BaseUseCase<LatLng, ResultWrapper<BaseDataWrapper<EventByLocationResponse>>>() {

    override fun execute(params: LatLng): Flow<ResultWrapper<BaseDataWrapper<EventByLocationResponse>>> {
        return flow {
            emit(eventRepository.getEventsByLocation(params))
        }
    }
}