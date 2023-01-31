package com.friendzrandroid.home.domain.interactor.chat.group

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.repository.MessageRepository
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendGroupMessageUseCase@Inject constructor(val repo: MessageRepository) :
    BaseUseCase<FormDataRequestWithImage, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: FormDataRequestWithImage): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.sendGroupMessage(params))
        }
    }
}