package com.friendzrandroid.core.paggingList

import android.content.Context
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.recyclerview.widget.RecyclerView
import com.friendzrandroid.R
import com.friendzrandroid.home.data.model.EventItemData

abstract class PagingAdapter<T>(
    private val viewModel: PagingViewModel<T>
) : RecyclerView.Adapter<BaseViewHolder<T>>(), OnDataLoaded<T> {
    init {
        viewModel.listener = this
    }

    val adapterList = ArrayList<BasePagingModel<T>>()
    override fun getItemCount() = adapterList.size


    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {

        if (adapterList.size > 0 && position == adapterList.size - 1) {
            viewModel.loadData()
        }
    }

//    fun showLoading(context: Context,@DimenRes viewHeight:Int?) {
//        if (viewHeight != null) {
//
//           val screen = context.resources.displayMetrics.heightPixels
//            val itemH = context.resources.getDimension(viewHeight).toInt()
//           val count = screen/ itemH
//
//            for (x in 0..count){
//                adapterList.add(BasePagingModel())
//            }
//
//        } else {
//            adapterList.addAll(
//                arrayListOf(
//                    BasePagingModel(),
//                    BasePagingModel(),
//                    BasePagingModel(),
//                    BasePagingModel()
//                )
//            )
//
//        }
//
//        notifyDataSetChanged()
//    }

    fun showLoading() {
        adapterList.addAll(
            arrayListOf(
                BasePagingModel(),
                BasePagingModel(),
                BasePagingModel(),
                BasePagingModel()
            )
        )
        notifyDataSetChanged()

    }

    fun showLoadingHorizontal() {
        adapterList.addAll(
            arrayListOf(
                BasePagingModel(),
                BasePagingModel(),

            )
        )
        notifyDataSetChanged()

    }

    fun reloadData() {
        adapterList.clear()
        notifyDataSetChanged()
//        showLoading()
        viewModel.reload()
    }

    override fun newData(list: List<T>) {
        adapterList.clear()
        adapterList.addAll(list.convertToBaseModel())
        notifyDataSetChanged()
    }

    override fun updateData(list: List<T>) {
        adapterList.addAll(list.convertToBaseModel())
        notifyDataSetChanged()
    }


    fun List<T>.convertToBaseModel(): List<BasePagingModel<T>> {
        val lit = ArrayList<BasePagingModel<T>>()
        for (item in this) {
            lit.add(BasePagingModel(DataType.DATA, item))
        }
        return lit
    }

}
