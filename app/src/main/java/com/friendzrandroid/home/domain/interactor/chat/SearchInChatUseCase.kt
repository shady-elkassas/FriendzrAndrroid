package com.friendzrandroid.home.domain.interactor.chat

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.InboxData
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.model.UsersInChatSearchResponse
import com.friendzrandroid.home.data.repository.MessageRepository
import com.friendzrandroid.home.domain.model.PagingListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchInChatUseCase@Inject constructor(val repo: MessageRepository):
    BaseUseCase<String, ResultWrapper<BaseDataWrapper<UsersInChatSearchResponse>>>() {

    override fun execute(params: String): Flow<ResultWrapper<BaseDataWrapper<UsersInChatSearchResponse>>> {
        return flow {
            emit(repo.searchInInbox(params))
        }
    }
}