package com.friendzrandroid.home.domain.interactor.tags

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.home.data.model.InterestData
import com.friendzrandroid.home.data.repository.AccountRepository
import com.friendzrandroid.home.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetIamUseCase @Inject constructor(val repo: AccountRepository) :
    BaseUseCase<Any, ResultWrapper<BaseDataWrapper<List<InterestData>>>>() {

    override fun execute(params: Any): Flow<ResultWrapper<BaseDataWrapper<List<InterestData>>>> {
        return flow {
            emit(repo.getMyIamTags())
        }
    }
}