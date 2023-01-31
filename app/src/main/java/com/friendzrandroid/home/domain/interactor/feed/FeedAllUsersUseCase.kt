package com.friendzrandroid.home.domain.interactor.feed

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.AllUsersResponse
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.repository.FeedRequestRepository
import com.friendzrandroid.home.domain.model.PagingListRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
//BaseUseCase<PagingListRequest,ResultWrapper<BaseDataWrapper<AllUsersResponse>>>
class FeedAllUsersUseCase @Inject constructor(private val repo:FeedRequestRepository) :BaseUseCase<PagingListRequest,ResultWrapper<BaseDataWrapper<AllUsersResponse>>>() {



    override fun execute(params: PagingListRequest): Flow<ResultWrapper<BaseDataWrapper<AllUsersResponse>>> {
        return flow {

            emit(repo.getFeedUsers(params.pageSize,params.PageNumber,params.degree,params.userLang,params.userLat,params.sortByInterestMatch))
        }

    }
}