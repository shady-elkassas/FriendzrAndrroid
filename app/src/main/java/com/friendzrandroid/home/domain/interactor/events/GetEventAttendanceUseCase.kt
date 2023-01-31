package com.friendzrandroid.home.domain.interactor.events

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.EventAttende
import com.friendzrandroid.home.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventAttendanceUseCase @Inject constructor(val repo: EventRepository) :
    BaseUseCase<String, ResultWrapper<BaseDataWrapper<EventAttende>>>() {

    override fun execute(params: String): Flow<ResultWrapper<BaseDataWrapper<EventAttende>>> {
        return flow {
            emit(repo.getEventAttendance(params))
        }
    }

}