package com.friendzrandroid.home.domain.interactor

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.NotificationsResponse
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.repository.MessageRepository
import com.friendzrandroid.home.domain.model.PagingListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNotificationsUseCase@Inject constructor(val repo: MessageRepository):
    BaseUseCase<PagingListRequest, ResultWrapper<BaseDataWrapper<NotificationsResponse>>>() {

    override fun execute(params: PagingListRequest): Flow<ResultWrapper<BaseDataWrapper<NotificationsResponse>>> {
        return flow {
            emit(repo.getNotifications(pageSize = params.pageSize,pageNumber = params.PageNumber))
        }
    }
}