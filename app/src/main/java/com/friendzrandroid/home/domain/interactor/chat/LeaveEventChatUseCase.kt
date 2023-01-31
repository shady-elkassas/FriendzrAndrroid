package com.friendzrandroid.home.domain.interactor.chat

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.repository.EventRepository
import com.friendzrandroid.home.domain.model.ChatOptionRequest
import com.friendzrandroid.home.domain.model.CickUserFromEventRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LeaveEventChatUseCase @Inject constructor(val repo: EventRepository) :
    BaseUseCase<String, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: String): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.leaveEventChat(params))
        }
    }

}