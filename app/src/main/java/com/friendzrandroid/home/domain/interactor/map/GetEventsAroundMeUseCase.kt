package com.friendzrandroid.home.domain.interactor.map

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventAroundMeResponse
import com.friendzrandroid.home.data.repository.MapRepository
import com.friendzrandroid.home.domain.model.MapFilterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventsAroundMeUseCase @Inject constructor(private val eventRepository: MapRepository) :
    BaseUseCase<MapFilterRequest, ResultWrapper<BaseDataWrapper<EventAroundMeResponse>>>() {
    override fun execute(par: MapFilterRequest): Flow<ResultWrapper<BaseDataWrapper<EventAroundMeResponse>>> {
        return flow {
            emit(eventRepository.getAroundMeEvents(par.latLang,par.filterSelectedTags))

        }
    }


}