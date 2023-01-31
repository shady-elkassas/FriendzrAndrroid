package com.friendzrandroid.core.paggingList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel

abstract class PagingViewModel<T> : BaseViewModel() {

    lateinit var listener: OnDataLoaded<T>
    val totalItemCount = MutableLiveData<Int>()
    val isEmptyList = MutableLiveData<Boolean>()
    protected val pageSize: Int = 20
    var pageNumber: Int = 1
    var searchText: String? = null
    protected var loadMore: Boolean = true

    abstract fun loadData()

    fun setData(list: List<T>?) {
        Log.e(
            "Loaded",
            "page number: ${pageNumber}   results: ${list!!.size}"
        )


        if (list == null || list.size == 0) {
            loadMore = false
        } else if (pageNumber == 1) {
            listener.newData(list)
            pageNumber++
        } else {
            listener.updateData(list)
            pageNumber++
        }


    }

    fun singleSetData(list: List<T>?) {
        if (list != null)
            listener.newData(list)
        loadMore = false
        pageNumber = 1
    }

    fun resetData(list: List<T>?) {

        if (list != null)
            listener.newData(list)
        loadMore = true
        pageNumber = 1

    }


    fun reload() {
        pageNumber = 1
        loadMore = true
        loadData()
    }









}