package com.friendzrandroid.core.paggingList

import java.io.Serializable

class BasePagingModel<T> (
    var type:DataType = DataType.LOADING,
    var data:T? = null

    )


enum class DataType(type: Int){
    LOADING(1),
    DATA(2)
}