package com.friendzrandroid.home.domain.interactor.userSettingsAndProfile

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.domain.usecase.BaseUseCase
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.model.UpdateUserLocationResponse
import com.friendzrandroid.home.data.repository.AccountRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateCurrentLocationUseCase @Inject constructor(val repo: AccountRepository) :
    BaseUseCase<LatLng, ResultWrapper<BaseDataWrapper<UpdateUserLocationResponse>>>() {
    override fun execute(params: LatLng): Flow<ResultWrapper<BaseDataWrapper<UpdateUserLocationResponse>>> {
        return flow {
            if (UserSessionManagement.getKeyAuthToken() != null && !UserSessionManagement.getKeyAuthToken()
                    .equals("")
            ) {
                emit(repo.updateCurrentLocation(params.latitude, params.longitude))
            }
        }
    }
}