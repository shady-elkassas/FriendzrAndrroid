package com.friendzrandroid.home.domain.interactor.events

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.data.model.EventsResponse
import com.friendzrandroid.home.data.repository.EventRepository
import com.friendzrandroid.home.domain.model.PagingListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMyEventsUseCase @Inject constructor(val repo: EventRepository) :
    BaseUseCase<PagingListRequest, ResultWrapper<BaseDataWrapper<EventsResponse>>>() {

    override fun execute(params: PagingListRequest): Flow<ResultWrapper<BaseDataWrapper<EventsResponse>>> {
        return flow {
            emit(repo.getEvents(params))
        }
    }

}