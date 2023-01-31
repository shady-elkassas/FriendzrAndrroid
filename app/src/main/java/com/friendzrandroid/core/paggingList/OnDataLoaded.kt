package com.friendzrandroid.core.paggingList

interface OnDataLoaded<T> {
    fun newData(list: List<T>)
    fun updateData(list: List<T>)
}