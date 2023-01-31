package com.friendzrandroid.home.domain.interactor.userSettingsAndProfile

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.data.model.UserSettingsResponse
import com.friendzrandroid.home.data.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserSettingsUseCase@Inject constructor(val repo:AccountRepository):BaseUseCase<Any,ResultWrapper<BaseDataWrapper<UserSettingsResponse>>>() {
    override fun execute(params: Any): Flow<ResultWrapper<BaseDataWrapper<UserSettingsResponse>>> {
        return flow {
            emit(repo.getUserSettings())
        }
    }
}