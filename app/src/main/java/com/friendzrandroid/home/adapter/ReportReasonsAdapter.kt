package com.friendzrandroid.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.friendzrandroid.R
import com.friendzrandroid.databinding.ItemReportReasonBinding
import com.friendzrandroid.home.data.model.ReportReasonModel

class ReportReasonsAdapter(
    private val listener: ReportReasonsCallBack? = null,
) : RecyclerView.Adapter<ReportReasonsAdapter.ViewHolder>() {

    private val reportReasonsList = ArrayList<ReportReasonModel>()

    private var currentSelectedItemPosition = -1

    fun setList(data: List<ReportReasonModel>) {
        reportReasonsList.clear()
        reportReasonsList.addAll(data)
        notifyDataSetChanged()
    }

    fun getList() = reportReasonsList


    inner class ViewHolder(val view: ItemReportReasonBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(data: ReportReasonModel, position: Int) {
            view.reportReasonCheck.text = data.name

            view.reportReasonCheck.isChecked = position == currentSelectedItemPosition

            view.reportReasonCheck.setOnClickListener {
                if (position == currentSelectedItemPosition) {
                    view.reportReasonCheck.isChecked = false
                    currentSelectedItemPosition = -1
                } else {
                    currentSelectedItemPosition = position
                    notifyDataSetChanged()
                }
                listener?.onReasonClicked(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = DataBindingUtil.inflate<ItemReportReasonBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_report_reason,
            parent,
            false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reportReasonsList[position], position)
    }

    override fun getItemCount(): Int = reportReasonsList.size

    interface ReportReasonsCallBack {
        fun onReasonClicked(reasonData: ReportReasonModel)
    }
}