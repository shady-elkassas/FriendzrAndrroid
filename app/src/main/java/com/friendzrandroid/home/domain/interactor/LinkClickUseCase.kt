package com.friendzrandroid.home.domain.interactor

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.data.model.UserSettingsResponse
import com.friendzrandroid.home.data.repository.AccountRepository
import com.friendzrandroid.home.domain.model.ChangePasswordRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LinkClickUseCase @Inject constructor(val repo: AccountRepository) :
    BaseUseCase<String, ResultWrapper<BaseDataWrapper<String>>>() {

    override fun execute(linkClickScreenName: String): Flow<ResultWrapper<BaseDataWrapper<String>>> {
        return flow {
            emit(repo.setLinkClick(linkClickScreenName))
        }
    }
}