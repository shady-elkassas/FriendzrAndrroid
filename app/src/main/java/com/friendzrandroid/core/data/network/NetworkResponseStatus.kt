package com.friendzrandroid.core.data.network

enum class NetworkResponseStatus(val status: Int) {

    SUCCESS(200),
    AUTHORIZATION(401),
    INTERNAL_SERVER_ERROR(406),
    INTERNAL_SERVER_ERROR_2(500),
    BAD_REQUEST(400),
    UN_SUPPORT_MEDIA(415),
    TEMPORARY_REDIRECT(307),
    CONFIRM_EMAIL(307),
    NOT_FOUND(404)


}