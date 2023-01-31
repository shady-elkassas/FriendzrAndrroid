package com.friendzrandroid.home.data.model

import java.io.Serializable

data class TagsModel(
    val tagID: String,
    val tagname: String
):Serializable {
    var isSelected = false
}