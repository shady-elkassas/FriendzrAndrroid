package com.friendzrandroid.home.domain.interactor.userSettingsAndProfile

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.data.repository.FeedRequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserProfileUseCase @Inject constructor(private val repo: FeedRequestRepository):BaseUseCase<String,ResultWrapper<BaseDataWrapper<UserProfileData>>>(){
    override fun execute(params: String): Flow<ResultWrapper<BaseDataWrapper<UserProfileData>>> {
        return flow{
            emit(repo.getUserProfile(params))
        }

    }


}