package com.friendzrandroid.home.domain.interactor.map

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventsResponse
import com.friendzrandroid.home.data.model.events.OnlyEventsAroundMeModel
import com.friendzrandroid.home.data.repository.EventRepository
import com.friendzrandroid.home.domain.model.PagingListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetOnlyEventsAroundMeUseCase @Inject constructor(val repo: EventRepository) :
    BaseUseCase<PagingListRequest, ResultWrapper<BaseDataWrapper<EventsResponse>>>() {

    override fun execute(params: PagingListRequest): Flow<ResultWrapper<BaseDataWrapper<EventsResponse>>> {
        return flow {
            emit(repo.getOnlyEventsAround(params))
        }
    }

}