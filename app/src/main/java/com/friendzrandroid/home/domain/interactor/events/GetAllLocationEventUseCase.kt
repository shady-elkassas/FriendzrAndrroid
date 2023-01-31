package com.friendzrandroid.home.domain.interactor.events

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventAllLocationResponse
import com.friendzrandroid.home.data.repository.EventRepository
import com.friendzrandroid.home.data.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllLocationEventUseCase@Inject constructor(private val eventRepository: MapRepository):BaseUseCase<Any, ResultWrapper<BaseDataWrapper<EventAllLocationResponse>>>() {
    override fun execute(params: Any): Flow<ResultWrapper<BaseDataWrapper<EventAllLocationResponse>>> {
        return flow {
            emit(eventRepository.getAllLocationEvent())
        }
    }
}