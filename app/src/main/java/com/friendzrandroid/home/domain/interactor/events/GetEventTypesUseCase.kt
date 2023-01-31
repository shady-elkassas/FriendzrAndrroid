package com.friendzrandroid.home.domain.interactor.events

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventTypesResponse
import com.friendzrandroid.home.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventTypesUseCase @Inject constructor(val repo: EventRepository) : BaseUseCase<Any, ResultWrapper<BaseDataWrapper<List<EventTypesResponse>>>>() {
    override fun execute(params: Any): Flow<ResultWrapper<BaseDataWrapper<List<EventTypesResponse>>>> {
        return flow {
            emit(repo.getEventTypes())
        }
    }
}