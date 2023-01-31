package com.friendzrandroid.home.domain.interactor.chat.group

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.repository.GroupChatRepository
import com.friendzrandroid.home.domain.model.ChatOptionRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MuteGroupChatUseCase @Inject constructor(val repo: GroupChatRepository) :
    BaseUseCase<ChatOptionRequest, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: ChatOptionRequest): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.muteChat(params))
        }
    }

}