package com.friendzrandroid.core.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.utils.SingleLiveEvent

abstract class BaseViewModel:ViewModel() {




    protected val _networkError: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var networkError: LiveData<Boolean> = _networkError

    protected val _serverError: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var serverError: LiveData<Boolean> = _serverError

    protected val _badRequestError: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var badRequestError: LiveData<Boolean> = _badRequestError

    protected val _authorizationError: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var authorizationError: LiveData<Boolean> = _authorizationError


    protected val _errorMessage: SingleLiveEvent<String> = SingleLiveEvent()
    var errorMessage: LiveData<String> = _errorMessage






    fun <T> validateResponse(response: ResultWrapper<T>):T? {
        when (response) {
            is ResultWrapper.NetworkError -> {
                _networkError.postValue(true)
                return response.errorVal
            }
            is ResultWrapper.ServerError -> {
                _serverError.postValue(true)
            }


            is ResultWrapper.BadRequest -> {
                _badRequestError.postValue(true)
            }

            is ResultWrapper.AuthorizationError -> {
                _authorizationError.postValue(true)
            }

            is ResultWrapper.GenericError -> {
                response.error?.message?.let {
                    _errorMessage.postValue(it)
                }
            }
            is ResultWrapper.InternalServerError ->{
                _serverError.postValue(true)
                return response.errorVal


            }
            is ResultWrapper.Success -> {
                return (response.value)
            }
            else -> {}
        }

        return null
    }



}