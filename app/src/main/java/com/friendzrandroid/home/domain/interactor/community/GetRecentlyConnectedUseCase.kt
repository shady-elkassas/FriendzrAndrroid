package com.friendzrandroid.home.domain.interactor.community

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.community.RecentlyConnectedResponse
import com.friendzrandroid.home.data.repository.CommunityRepository
import com.friendzrandroid.home.domain.model.PagingListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecentlyConnectedUseCase @Inject constructor(val repo: CommunityRepository) :
    BaseUseCase<PagingListRequest, ResultWrapper<BaseDataWrapper<RecentlyConnectedResponse>>>() {

    override fun execute(params: PagingListRequest): Flow<ResultWrapper<BaseDataWrapper<RecentlyConnectedResponse>>> {
        return flow {
            emit(repo.getRecentlyConnected(params))
        }
    }

}