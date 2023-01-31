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

class ForgetPasswordUseCase @Inject constructor(private val repo: AuthRepository) :BaseUseCase<String, ResultWrapper<BaseDataWrapper<Any>>>() {

    override fun execute(params: String): Flow<ResultWrapper<BaseDataWrapper<Any>>> {
        return flow {
            emit(repo.forgetPassword(params))


        }
    }
}