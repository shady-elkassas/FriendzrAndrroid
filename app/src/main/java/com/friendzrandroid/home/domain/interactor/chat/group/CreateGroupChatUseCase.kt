package com.friendzrandroid.home.domain.interactor.chat.group

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.groupchat.GroupChatResponse
import com.friendzrandroid.home.data.repository.GroupChatRepository
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateGroupChatUseCase @Inject constructor(val repo: GroupChatRepository) :
    BaseUseCase<FormDataRequestWithImage, ResultWrapper<BaseDataWrapper<GroupChatResponse>>>() {

    override fun execute(params: FormDataRequestWithImage): Flow<ResultWrapper<BaseDataWrapper<GroupChatResponse>>> {
        return flow {
            emit(repo.createGroup(params))
        }
    }

}