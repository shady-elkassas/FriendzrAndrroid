package com.friendzrandroid.home.fragment.more.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.model.ConfigurationValidation
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.UserSettingsResponse
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.*
import com.friendzrandroid.home.domain.model.UserSettingsRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsUseCase: UserSettingsUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val updateSettingsUseCase: UpdateUserSettingsUseCase,
    private val getConfigurationValidationUseCase: GetConfigurationValidationUseCase,
    val logoutUseCase: LogoutAccountUseCase
) : BaseViewModel() {

    var hideFrom: ArrayList<Int> = ArrayList(0)

    val settingResponseState = MutableLiveData<GetSettingsDataState>()
    val privateModeStringList = MutableLiveData<List<String>>()
    val settingsUpdated = MutableLiveData<UpdateSettingsDataState>()
    val configurationResponse = MutableLiveData<DataState<ConfigurationValidation>>()

    fun getSettings() {
        userSettingsUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response.isSuccessful) {

                    settingResponseState.value = GetSettingsDataState.Success(response.model!!)

                } else {
                    settingResponseState.value = GetSettingsDataState.Fail(response.message)
                }

            } else {
                settingResponseState.value =
                    GetSettingsDataState.Fail("something went wrong try again later")

            }

        }.launchIn(viewModelScope)
    }


    fun deleteAccount() {
        deleteAccountUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response.isSuccessful) {
                    UserSessionManagement.removeUserSession()
                    _authorizationError.postValue(true)
                } else {
                    settingResponseState.value = GetSettingsDataState.Fail(response.message)
                }

            } else {
                settingResponseState.value =
                    GetSettingsDataState.Fail("something went wrong try again later")

            }

        }.launchIn(viewModelScope)
    }


    fun updateSettings(
        pushnotification: Boolean,
        allowmylocation: Boolean,
        ghostmode: Boolean,
        manualdistancecontrol: Double,
        filteringaccordingtoage: Boolean,
        agefrom: Int,
        ageto: Int,
        myAppearanceTypes: String,
        personalSpace: Boolean,
        distanceFilter: Boolean
    ) {
        updateSettingsUseCase.execute(
            UserSettingsRequest(
                pushnotification,
                allowmylocation,
                ghostmode,
                manualdistancecontrol,
                filteringaccordingtoage,
                agefrom,
                ageto,
                myAppearanceTypes,
                personalSpace,
                distanceFilter
            )
        ).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response.isSuccessful) {
                    UserSessionManagement.saveUserSettings(response.model!!)
                    settingsUpdated.value = UpdateSettingsDataState.Success(response.model)
                } else {
                    settingsUpdated.value = UpdateSettingsDataState.Fail(response.message)
                }

            } else {
                settingsUpdated.value =
                    UpdateSettingsDataState.Fail("something went wrong try again later")

            }

        }.launchIn(viewModelScope)

    }

    fun getConfigurationValidation() {
        getConfigurationValidationUseCase.execute(Any())
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful)
                        configurationResponse.postValue(DataState.NewData(it.model!!))
                    else
                        configurationResponse.postValue(DataState.FailData(it.message))
                }
            }.launchIn(viewModelScope)
    }

    sealed class GetSettingsDataState {
        class Success(val data: UserSettingsResponse) : GetSettingsDataState()
        class Fail(val message: String) : GetSettingsDataState()
    }

    sealed class UpdateSettingsDataState {
        class Success(val data: UserSettingsResponse) : UpdateSettingsDataState()
        class Fail(val message: String) : UpdateSettingsDataState()
    }
}