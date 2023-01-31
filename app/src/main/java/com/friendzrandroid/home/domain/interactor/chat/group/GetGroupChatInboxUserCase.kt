package com.friendzrandroid.home.domain.interactor.chat.group

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.UserChatResponse
import com.friendzrandroid.home.data.model.GroupChatMsgResponse
import com.friendzrandroid.home.data.repository.GroupChatRepository
import com.friendzrandroid.home.domain.model.PagingMessageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGroupChatInboxUserCase @Inject constructor(val repo: GroupChatRepository) :
    BaseUseCase<PagingMessageRequest, ResultWrapper<BaseDataWrapper<GroupChatMsgResponse>>>() {

    override fun execute(params: PagingMessageRequest): Flow<ResultWrapper<BaseDataWrapper<GroupChatMsgResponse>>> {
        return flow {
            emit(repo.getGroupChat(params.pageSize, params.PageNumber, params.chatID))
        }
    }
}