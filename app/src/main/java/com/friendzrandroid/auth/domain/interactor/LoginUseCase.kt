package com.friendzrandroid.auth.domain.interactor

import com.friendzrandroid.auth.data.model.LoginResponseData
import com.friendzrandroid.auth.data.repository.AuthRepository
import com.friendzrandroid.auth.domain.model.LoginRequest
import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) :BaseUseCase<LoginRequest, ResultWrapper<BaseDataWrapper<LoginResponseData>>>() {

    override fun execute(params: LoginRequest): Flow<ResultWrapper<BaseDataWrapper<LoginResponseData>>> {
        return flow {
            emit(repo.login(params.email,params.password,params.loginType,params.fcmToken,params.userId,params.platform))


        }
    }
}