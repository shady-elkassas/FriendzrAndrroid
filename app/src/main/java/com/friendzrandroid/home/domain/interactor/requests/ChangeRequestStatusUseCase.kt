package com.friendzrandroid.home.domain.interactor.requests

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.repository.FeedRequestRepository
import com.friendzrandroid.home.domain.model.ChangeFeedStatusRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChangeRequestStatusUseCase @Inject constructor(private val repo: FeedRequestRepository) {

    suspend fun execute(params: ChangeFeedStatusRequest): ResultWrapper<BaseDataWrapper<Any>> {
        return repo.updateUserStatus(params.userID, params.key, params.isNotFriend )
    }
}