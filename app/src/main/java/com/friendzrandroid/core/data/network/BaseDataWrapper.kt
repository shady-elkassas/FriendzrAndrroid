package com.friendzrandroid.core.data.network

data class BaseDataWrapper<T>(
    val message: String,
    val isSuccessful: Boolean,
    val model: T?,
    var statusCode: Int
)