package com.friendzrandroid.home.domain

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.repository.MessageRepository
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.friendzrandroid.home.domain.model.PagingListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendUserMessageUseCase@Inject constructor(val repo: MessageRepository):
    BaseUseCase<FormDataRequestWithImage, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: FormDataRequestWithImage): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.sendUserMessage(params))
        }
    }
}