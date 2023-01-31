package com.friendzrandroid.home.domain.interactor.chat

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.repository.MessageRepository
import com.friendzrandroid.home.domain.model.ChatOptionRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MuteChatUseCase @Inject constructor(val repo: MessageRepository) :
    BaseUseCase<ChatOptionRequest, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: ChatOptionRequest): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.muteChat(params))
        }
    }
}