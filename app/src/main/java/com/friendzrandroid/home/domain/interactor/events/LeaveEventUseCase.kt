package com.friendzrandroid.home.domain.interactor.events

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.data.repository.EventRepository
import com.friendzrandroid.home.domain.model.LeaveEventRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LeaveEventUseCase @Inject constructor(val repo: EventRepository) :
    BaseUseCase<LeaveEventRequest, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: LeaveEventRequest): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.leaveEvent(params.eventId, params.leaveDate, params.leaveTime))
        }
    }
}