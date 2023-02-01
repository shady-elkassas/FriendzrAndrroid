package com.friendzrandroid.home.dialog.mapFilterDialog

import com.friendzrandroid.home.data.model.TagsModel

interface MapFilterDialogListener {
    fun onMapFilterSave(
        selectedTags: ArrayList<TagsModel>,
        dateCriteria: String?,
        startDate: String?,
        endDate: String?
    )

    fun onMapFilterDismiss(isDismiss: Boolean)
}