package com.friendzrandroid.home.data.model

sealed class DataState<T> {

    data class Loading<T>(val data:T?): DataState<T>()
    data class NewData<T>(val data: T) : DataState<T>()
    data class UpdateData<T>(val data: T) : DataState<T>()
    data class FailData<T>(val message:String) : DataState<T>()

}