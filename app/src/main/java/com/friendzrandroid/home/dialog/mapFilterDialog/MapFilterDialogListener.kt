package com.friendzrandroid.home.dialog.mapFilterDialog

import com.friendzrandroid.home.data.model.TagsModel

interface MapFilterDialogListener {
    fun onMapFilterSave( selectedTags: ArrayList<TagsModel>)

    fun onMapFilterDismiss( isDismiss:Boolean)
}