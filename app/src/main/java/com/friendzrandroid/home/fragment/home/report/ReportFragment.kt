package com.friendzrandroid.home.fragment.home.report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.hideButtonLoading
import com.friendzrandroid.core.utils.showButtonLoading
import com.friendzrandroid.databinding.FragmentUserReportBinding
import com.friendzrandroid.home.adapter.ReportReasonsAdapter
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.ReportReasonModel
import com.friendzrandroid.home.data.model.enum.ReportStates
import com.friendzrandroid.home.domain.model.SendReportRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportFragment : BaseFragment(), ReportReasonsAdapter.ReportReasonsCallBack {

    private val TAG = "ReportFragment"

    val args: ReportFragmentArgs by navArgs()

    val id by lazy { args.userId }
    private val reportType by lazy { args.reportType }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentUserReportBinding.inflate(layoutInflater)
    }

    private val viewModel: ReportViewModel by viewModels()

    private lateinit var reasonsAdapter: ReportReasonsAdapter

    private var selectedReason: ReportReasonModel = ReportReasonModel("", "")

    override val baseViewModel: BaseViewModel
        get() = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getAllReportReasons()

        setClicks()
        setObservers()
        setReasonsRecycler()

        return binding.root
    }

    private fun setObservers() {
        viewModel.reportReasons.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> showLoading()
                is DataState.NewData -> {
                    hideLoading()
                    reasonsAdapter.setList(it.data)
                }
                is DataState.FailData -> {
                    hideLoading()
                    showToast(it.message)
                }
                else -> hideLoading()
            }
        }

        viewModel.reportSendState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    //showToast("Report sent")
                    binding.reportSubmitButton.hideButtonLoading(
                        resources.getString(R.string.title_sent),
                        requireActivity()
                    ) {
                        findNavController().popBackStack()
                    }
                }
                is DataState.FailData -> {
                    binding.reportSubmitButton.showButtonLoading(resources.getString(R.string.submit))
                    showToast(it.message)
                }
                else -> {}
            }
        }

    }

    private fun setClicks() {
        binding.reportSubmitButton.setOnClickListener {

            if (selectedReason.id == "") {
                showToast("Please select a reason for reporting.")
            } else {
                val requestBody =
                    when (reportType) {
                        ReportStates.REPORT_USER.value -> SendReportRequest(
                            id,
                            false,
                            binding.reportReasonMessage.text.toString(),
                            selectedReason.id,
                            reportType
                        )

                        ReportStates.REPORT_EVENT.value -> SendReportRequest(
                            id,
                            true,
                            binding.reportReasonMessage.text.toString(),
                            selectedReason.id,
                            reportType
                        )
                        else -> SendReportRequest(
                            id,
                            false,
                            binding.reportReasonMessage.text.toString(),
                            selectedReason.id,
                            reportType
                        )
                    }

                Log.e(TAG, "setClicks: $requestBody")

                binding.reportSubmitButton.showButtonLoading(resources.getString(R.string.title_submitting))
                viewModel.sendReport(requestBody)
            }
        }

        binding.imgEditProfileBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setReasonsRecycler() {

        reasonsAdapter = ReportReasonsAdapter(this)
        binding.reportReasonsRecycler.apply {
            adapter = reasonsAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            hasFixedSize()
        }
    }

    override fun onReasonClicked(reasonData: ReportReasonModel) {
        Log.i(TAG, "onReasonClicked: $reasonData")

        selectedReason = if (reasonData != selectedReason) reasonData
        else ReportReasonModel("", "")
    }
}