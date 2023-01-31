package com.friendzrandroid.home.dialog.filterDialog

interface FilterDialogListener {
    fun onSave(ageFrom: Int, ageTo: Int, distance: Double, isAgeSlider: Boolean)
    fun onDismissed(isAgeFilter: Boolean)
}