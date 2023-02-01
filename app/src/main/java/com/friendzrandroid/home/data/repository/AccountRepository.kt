package com.friendzrandroid.home.data.repository

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.datasource.AccountAPIS
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.friendzrandroid.home.domain.model.UserSettingsRequest
import okhttp3.MultipartBody
import javax.inject.Inject

class AccountRepository @Inject constructor(private val api: AccountAPIS) : SafeApiRequest() {


    suspend fun updateAdditionalImages(images: List<MultipartBody.Part>): ResultWrapper<Any> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {
            api.updateAdditionalImages(token!!, images)
        }
    }

    suspend fun getMyProfile(): ResultWrapper<BaseDataWrapper<UserProfileData>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest { api.getProfileData(token!!) } as ResultWrapper<BaseDataWrapper<UserProfileData>>
    }


    suspend fun getConfigurationValidation(): ResultWrapper<BaseDataWrapper<ConfigurationValidation>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getConfigurationValidation(token!!)
        } as ResultWrapper<BaseDataWrapper<ConfigurationValidation>>
    }

    suspend fun getMyIamTags(): ResultWrapper<BaseDataWrapper<List<InterestData>>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest { api.getIamTags(token!!) } as ResultWrapper<BaseDataWrapper<List<InterestData>>>
    }

    suspend fun getPreferTags(): ResultWrapper<BaseDataWrapper<List<InterestData>>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest { api.getPreferTags(token!!) } as ResultWrapper<BaseDataWrapper<List<InterestData>>>
    }

    suspend fun updateMyProfile(data: FormDataRequestWithImage): ResultWrapper<BaseDataWrapper<UserProfileData>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.updateProfileData(
                token!!,
                data.userData,
                data.UserImags
            )
        } as ResultWrapper<BaseDataWrapper<UserProfileData>>


    }

    suspend fun getUserSettings(): ResultWrapper<BaseDataWrapper<UserSettingsResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest { api.getUserSettings(token!!) } as ResultWrapper<BaseDataWrapper<UserSettingsResponse>>
    }

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {
            api.changePassword(
                token!!,
                newPassword,
                oldPassword
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun setLinkClick(
        linkClickScreenName: String
    ): ResultWrapper<BaseDataWrapper<String>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {
            api.linkClick(
                token!!,
                linkClickScreenName
            )
        } as ResultWrapper<BaseDataWrapper<String>>
    }

    suspend fun deleteAccount(): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest { api.deleteAccount(token!!) } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun logoutAccount(): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest { api.logoutAccount(token!!) } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun updateCurrentLocation(
        lat: Double,
        lang: Double
    ): ResultWrapper<BaseDataWrapper<UpdateUserLocationResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {

            api.updateCurrentLocation(
                token!!,
                lang,
                lat
            )


        } as ResultWrapper<BaseDataWrapper<UpdateUserLocationResponse>>
    }

    suspend fun updateUserSettings(params: UserSettingsRequest): ResultWrapper<BaseDataWrapper<UserSettingsResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {

            api.updateUserSettings(
                token!!,
                params.pushnotification!!,
                params.allowmylocation!!,
                params.ghostmode!!,
                params.myAppearanceTypes!!,
                params.manualdistancecontrol!!,
                params.filteringaccordingtoage!!,
                params.agefrom!!,
                params.ageto!!,
                params.personalSpace!!,
                params.distanceFilter!!
            )
        } as ResultWrapper<BaseDataWrapper<UserSettingsResponse>>
    }
}