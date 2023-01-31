package com.friendzrandroid.core.data.network

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class InternalServerError<out T : BaseDataWrapper<Nothing>>(val errorVal: T) :
        ResultWrapper<T>()

    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null) :
        ResultWrapper<Nothing>()

    data class NetworkError<out T : BaseDataWrapper<Nothing>>(val errorVal: T) : ResultWrapper<T>()
    object ServerError : ResultWrapper<Nothing>()
    object AuthorizationError : ResultWrapper<Nothing>()
    object BadRequest : ResultWrapper<Nothing>()

    object NotFound : ResultWrapper<Nothing>()
    object TemporaryRedirect : ResultWrapper<Nothing>()
    object ConfirmEmail : ResultWrapper<Nothing>()


}