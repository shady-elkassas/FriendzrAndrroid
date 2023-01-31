package com.friendzrandroid.home.domain.interactor.chat

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.EventChatResponse
import com.friendzrandroid.home.data.model.NotificationsResponse
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.repository.MessageRepository
import com.friendzrandroid.home.domain.model.PagingListRequest
import com.friendzrandroid.home.domain.model.PagingMessageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventChatDataUseCase@Inject constructor(val repo: MessageRepository):
    BaseUseCase<PagingMessageRequest, ResultWrapper<BaseDataWrapper<EventChatResponse>>>() {

    override fun execute(params: PagingMessageRequest): Flow<ResultWrapper<BaseDataWrapper<EventChatResponse>>> {
        return flow {
            emit(repo.getEventMessages(pageSize = params.pageSize,pageNumber = params.PageNumber,eventID = params.chatID))
        }
    }
}