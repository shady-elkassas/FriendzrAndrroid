package com.friendzrandroid.home.domain.interactor.community

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.community.RecommendedEventResponse
import com.friendzrandroid.home.data.model.community.RecommendedPeopleResponse
import com.friendzrandroid.home.data.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecommendedEventsUseCase @Inject constructor(val repo: CommunityRepository) :
    BaseUseCase<String, ResultWrapper<BaseDataWrapper<RecommendedEventResponse>>>() {


    override fun execute(eventId: String): Flow<ResultWrapper<BaseDataWrapper<RecommendedEventResponse>>> {
        return flow {
            emit(repo.getRecommendEvents(eventId))
        }
    }
}