package com.friendzrandroid.core.data.network

import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): ResultWrapper<Any> {
        try {
            val response = call.invoke()

            return if (response.code().equals(NetworkResponseStatus.SUCCESS.status)) {
                var responseData = response.body() as BaseDataWrapper<Any>
                if (responseData.statusCode!!.equals(NetworkResponseStatus.SUCCESS.status)) {
                    ResultWrapper.Success(response.body()!!)
                } else {
                    if (responseData.statusCode!! != NetworkResponseStatus.AUTHORIZATION.status) {
                        var errorResponse = ErrorResponse(responseData.message)
                        ResultWrapper.GenericError(responseData.statusCode, errorResponse)
                    } else {
                        ResultWrapper.AuthorizationError
                    }

                }
            } else if (response.code().equals(NetworkResponseStatus.AUTHORIZATION.status)) {
                ResultWrapper.AuthorizationError
            } else if (response.code().equals(NetworkResponseStatus.BAD_REQUEST.status)) {
                ResultWrapper.BadRequest
            } else if (response.code().equals(NetworkResponseStatus.INTERNAL_SERVER_ERROR.status) ||
                response.code().equals(NetworkResponseStatus.INTERNAL_SERVER_ERROR_2.status) ||
                response.code().equals(NetworkResponseStatus.BAD_REQUEST.status) ||
                response.code().equals(NetworkResponseStatus.UN_SUPPORT_MEDIA.status) ||
                response.code().equals(NetworkResponseStatus.TEMPORARY_REDIRECT.status) ||
                response.code().equals(NetworkResponseStatus.CONFIRM_EMAIL.status) ||
                response.code().equals(NetworkResponseStatus.NOT_FOUND.status)
            ) {

                val errBody = JSONObject(response.errorBody()?.charStream()?.readText()!!)
                val message = errBody.getString("message")
                val statusCode = errBody.getInt("statusCode")
                val bol = errBody.getBoolean("isSuccessful")

                //Log.e(TAG, "Error: ${message}   , ${statusCode}    , ${bol}")

                ResultWrapper.InternalServerError(
                    BaseDataWrapper<Nothing>(
                        message,
                        bol,
                        null,
                        statusCode
                    )
                )
            } else {
                ResultWrapper.ServerError
            }
        } catch (throwable: Throwable) {
            return when (throwable) {
                is IOException -> ResultWrapper.NetworkError(
                    BaseDataWrapper<Nothing>(
                        "no network data",
                        false,
                        null,
                        400
                    )
                )

                is HttpException -> {
                    val code = throwable.code()
                    if (code != NetworkResponseStatus.AUTHORIZATION.status) {
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    } else {
                        ResultWrapper.AuthorizationError
                    }
                }

                else -> {
                    ResultWrapper.GenericError(
                        null,
                        ErrorResponse("some error happen, try again later")
                    )
                }
            }
        }

    }


    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.charStream()?.let {
                Gson().fromJson(it, ErrorResponse::class.java)

            }
        } catch (exception: Exception) {
            null
        }
    }
}



