package com.friendzrandroid.home.domain.model

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Part
import java.util.*

class FormDataRequestWithImage(

   val userData:HashMap<String, RequestBody>,
    val UserImags: MultipartBody.Part?
)


