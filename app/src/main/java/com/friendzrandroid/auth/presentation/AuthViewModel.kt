package com.friendzrandroid.auth.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum
import com.friendzrandroid.auth.domain.interactor.ForgetPasswordUseCase
import com.friendzrandroid.auth.domain.interactor.LoginUseCase
import com.friendzrandroid.auth.domain.interactor.RegisterUseCase
import com.friendzrandroid.auth.domain.model.LoginRequest
import com.friendzrandroid.auth.domain.model.RegisterRequest
import com.friendzrandroid.core.data.network.NetworkResponseStatus
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.model.enum.NeedToUpdateStatus
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.MyProfileUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val myProfileUseCase: MyProfileUseCase,
    private val registerUseCase: RegisterUseCase,
    private val forgetPasswordUseCase: ForgetPasswordUseCase
) : BaseViewModel() {

    private val _isSuccessful = MutableLiveData<LoginRegisterStatus>()

    val isAuthSuccessful: LiveData<LoginRegisterStatus> = _isSuccessful

    fun performLogin(
        email: String,
        password: String,
        loginType: LoginRegisterTypeEnum,
        userId: String,
        platform: LoginRegisterTypeEnum
    ) {

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener {
                if (!it.isSuccessful()) {

                    _isSuccessful.value = LoginRegisterStatus.Fail(it.result)

                } else {
                    val token = it.getResult()
                    login.execute(
                        LoginRequest(
                            email,
                            password,
                            token,
                            loginType,
                            userId,
                            platform
                        )
                    ).flowOn(Dispatchers.IO).onEach {


                        val response = validateResponse(it)
                        response?.let {
                            if (it.isSuccessful) {

                                //save user model in the session
                                response.model?.let { it1 -> UserSessionManagement.saveUserData(it1) }

                                if (response.model!!.isWhiteLable) {
                                    _isSuccessful.value = LoginRegisterStatus.IsWhiteLabel()

                                } else if (response.model!!.needUpdate == NeedToUpdateStatus.UPDATE_PROFILE.status) {
                                    _isSuccessful.value = LoginRegisterStatus.needToUpdate()

                                } else
                                    _isSuccessful.value = LoginRegisterStatus.IsSuccessful()


//                                getMyProfile()


                            } else {
                                _isSuccessful.value = LoginRegisterStatus.Fail(it.message)

                            }

                        }
                    }.launchIn(viewModelScope)
                }
            }
    }


    // we should delete this  because there is returned key from login its name is "needUpdate"
    /*  private fun getMyProfile() {

          myProfileUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

              val response = validateResponse(it)
              if (response != null) {
                  if (response.isSuccessful) {
                      UserSessionManagement.saveUserData(response.model!!)
                      if (response.model!!.needUpdate==1){
                          _isSuccessful.value = LoginRegisterStatus.needToUpdate(response.model!!)
                      }else
                      _isSuccessful.value = LoginRegisterStatus.IsSuccessful()



                  } else {
                      _isSuccessful.value = LoginRegisterStatus.Fail(response.message)
                  }

              } else {
                  _isSuccessful.value =
                      LoginRegisterStatus.Fail("something went wrong try again later")

              }

          }.launchIn(viewModelScope)
      }*/


    fun performRegister(
        username: String,
        email: String,
        password: String,
        loginType: LoginRegisterTypeEnum,
        userId: String,
        platFormType: LoginRegisterTypeEnum
    ) {
        registerUseCase.execute(
            RegisterRequest(
                username,
                email,
                password,
                loginType,
                userId,
                platFormType
            )
        ).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {
                        //save user model in the session
                        response.model?.let { it1 -> UserSessionManagement.saveUserToken(it1.token) }
                        _isSuccessful.value = LoginRegisterStatus.IsSuccessful()
                    } else if (it.statusCode == NetworkResponseStatus.INTERNAL_SERVER_ERROR.status) {
                        _isSuccessful.value = LoginRegisterStatus.Fail(it.message)

                    }
                }
            }.launchIn(viewModelScope)
    }


    fun forgetPassword(
        email: String
    ) {
        forgetPasswordUseCase.execute(
            email
        ).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {

                        _isSuccessful.value = LoginRegisterStatus.IsSuccessful()


                    } else {
                        _isSuccessful.value = LoginRegisterStatus.Fail(it.message)

                    }
                }
            }.launchIn(viewModelScope)
    }

}


sealed class LoginRegisterStatus {
    class IsWhiteLabel() : LoginRegisterStatus()
    class IsSuccessful() : LoginRegisterStatus()
    class needToUpdate() : LoginRegisterStatus()

    //    class needToUpdate(val userData: LoginResponseData):LoginRegisterStatus()
    class Fail(val message: String) : LoginRegisterStatus()
}