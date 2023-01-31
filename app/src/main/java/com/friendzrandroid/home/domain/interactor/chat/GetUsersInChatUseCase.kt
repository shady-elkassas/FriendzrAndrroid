package com.friendzrandroid.home.domain.interactor.chat

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.repository.MessageRepository
import com.friendzrandroid.home.domain.model.PagingListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUsersInChatUseCase@Inject constructor(val repo: MessageRepository):
    BaseUseCase<PagingListRequest, ResultWrapper<BaseDataWrapper<UsersInChatDataResponse>>>() {

    override fun execute(params: PagingListRequest): Flow<ResultWrapper<BaseDataWrapper<UsersInChatDataResponse>>> {
        return flow {
            emit(repo.getUsersInChat(pageSize = params.pageSize,pageNumber = params.PageNumber, search = params.search))
        }
    }
}