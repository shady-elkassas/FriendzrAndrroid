package com.friendzrandroid.home.dialog.tagsDialog

import com.friendzrandroid.home.data.model.TagsModel

interface TagDialogListener {
    fun onSave(tagType: Int, selectedTags: ArrayList<TagsModel>)
}