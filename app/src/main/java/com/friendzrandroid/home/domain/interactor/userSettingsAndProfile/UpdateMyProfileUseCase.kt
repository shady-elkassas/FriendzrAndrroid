package com.friendzrandroid.home.domain.interactor.userSettingsAndProfile

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.data.repository.AccountRepository
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateMyProfileUseCase@Inject constructor(val repo: AccountRepository):
    BaseUseCase<FormDataRequestWithImage, ResultWrapper<BaseDataWrapper<UserProfileData>>>() {
    override fun execute(params: FormDataRequestWithImage): Flow<ResultWrapper<BaseDataWrapper<UserProfileData>>> {
        return flow {

            emit(repo.updateMyProfile(params))
        }
    }
}