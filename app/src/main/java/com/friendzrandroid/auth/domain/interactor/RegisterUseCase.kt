package com.friendzrandroid.auth.domain.interactor

import com.friendzrandroid.auth.data.model.RegistrationResponse
import com.friendzrandroid.auth.data.repository.AuthRepository
import com.friendzrandroid.auth.domain.model.RegisterRequest
import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repo: AuthRepository) :
    BaseUseCase<RegisterRequest, ResultWrapper<BaseDataWrapper<RegistrationResponse>>>() {


    override fun execute(params: RegisterRequest): Flow<ResultWrapper<BaseDataWrapper<RegistrationResponse>>> {
        return flow {
            emit(
                repo.register(
                    params.userName,
                    params.email,
                    params.password,
                    params.loginType,
                    params.userId,
                    params.platform
                )
            )
        }
    }
}