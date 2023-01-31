package com.friendzrandroid.home.domain.interactor.chat.group

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.repository.GroupChatRepository
import com.friendzrandroid.home.domain.model.UserWithGroupRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserToGroupUseCase @Inject constructor(val repo: GroupChatRepository) :
    BaseUseCase<UserWithGroupRequest, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: UserWithGroupRequest): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.addUserToChat(params.id, params.date, params.listOfUsers))
        }
    }
}