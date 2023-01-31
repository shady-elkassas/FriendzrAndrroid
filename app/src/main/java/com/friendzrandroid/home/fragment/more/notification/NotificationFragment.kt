package com.friendzrandroid.home.fragment.more.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.NotificationFragmentBinding
import com.friendzrandroid.home.adapter.NotificationsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NotificationFragment : BaseFragment() {

    private val viewModel: NotificationViewModel by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        NotificationFragmentBinding.inflate(layoutInflater)
    }
    override val baseViewModel: BaseViewModel
        get() = viewModel

    private lateinit var recAdapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.totalItemCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.recNotif.show()
                binding.noDataContainer.hide()
            } else {
                binding.noDataContainer.show()
                binding.recNotif.hide()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (!this::recAdapter.isInitialized) {
            initRecyclerView()
        } else {
            recAdapter.reloadData()
        }
    }


    private fun initRecyclerView() {

        recAdapter = NotificationsAdapter(viewModel)


        binding.recNotif.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recAdapter.showLoading()

    }

}