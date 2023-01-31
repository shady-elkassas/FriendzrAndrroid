package com.friendzrandroid.home.domain.interactor.community

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.community.RecommendedPeopleResponse
import com.friendzrandroid.home.data.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecommendedPeopleUseCase @Inject constructor(val repo: CommunityRepository) :
    BaseUseCase<String, ResultWrapper<BaseDataWrapper<RecommendedPeopleResponse>>>() {


    override fun execute(userId: String): Flow<ResultWrapper<BaseDataWrapper<RecommendedPeopleResponse>>> {
        return flow {
            emit(repo.getRecommendPeople(userId))
        }
    }
}