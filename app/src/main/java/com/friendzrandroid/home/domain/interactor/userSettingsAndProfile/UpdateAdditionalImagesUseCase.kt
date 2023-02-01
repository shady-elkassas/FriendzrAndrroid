package com.friendzrandroid.home.domain.interactor.userSettingsAndProfile

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.data.model.UserSettingsResponse
import com.friendzrandroid.home.data.repository.AccountRepository
import com.friendzrandroid.home.domain.model.ChangePasswordRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class UpdateAdditionalImagesUseCase @Inject constructor(val repo: AccountRepository) :
    BaseUseCase<List<MultipartBody.Part>, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(images: List<MultipartBody.Part>): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.updateAdditionalImages(images) as ResultWrapper<BaseDataWrapper<Any>>)
        }
    }
}