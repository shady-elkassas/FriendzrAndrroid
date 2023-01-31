package com.friendzrandroid.home.domain.interactor.events

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.data.repository.AccountRepository
import com.friendzrandroid.home.data.repository.EventRepository
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateEventUseCase@Inject constructor(val repo: EventRepository):
    BaseUseCase<FormDataRequestWithImage, ResultWrapper<BaseDataWrapper<Any>>>() {
    override fun execute(params: FormDataRequestWithImage): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {

            emit(repo.EditEvent(params))
        }
    }
}