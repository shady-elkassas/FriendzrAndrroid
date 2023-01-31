package com.friendzrandroid.home.domain.interactor.events

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventDetails
import com.friendzrandroid.home.data.repository.EventRepository
import com.friendzrandroid.home.domain.model.CickUserFromEventRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CickUserFromEventUseCase@Inject constructor(val repo: EventRepository) :
    BaseUseCase<CickUserFromEventRequest, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: CickUserFromEventRequest): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.CickOutUserFromEvent(params.eventID,params.AttendUserID,params.status.value))
        }
    }

}