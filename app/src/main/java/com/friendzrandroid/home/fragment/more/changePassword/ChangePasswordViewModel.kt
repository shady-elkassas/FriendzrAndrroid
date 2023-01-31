package com.friendzrandroid.home.fragment.more.changePassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.ChangePasswordUseCase
import com.friendzrandroid.home.domain.model.ChangePasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : BaseViewModel() {

    val failMessage = MutableLiveData<String>()
    val changePasswordState: MutableLiveData<Boolean> = MutableLiveData()

    fun changePassword(newPassword: String, oldPassword: String) {
        changePasswordUseCase.execute(ChangePasswordRequest(newPassword, oldPassword))
            .flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response.isSuccessful) {
                    changePasswordState.postValue(true)
                    //UserSessionManagement.removeUserSession()
                    //_authorizationError.postValue(true)
                } else {
                    failMessage.postValue(response.message)
                }

            } else {
                failMessage.postValue("something went wrong try again later")

            }

        }.launchIn(viewModelScope)
    }

}