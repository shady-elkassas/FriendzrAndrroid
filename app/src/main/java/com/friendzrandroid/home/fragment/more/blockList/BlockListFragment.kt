package com.friendzrandroid.home.fragment.more.blockList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.AllUsersFragmentBinding
import com.friendzrandroid.home.adapter.BlockListAdapter
import com.friendzrandroid.home.adapter.listener.FeedRequestAdapterListener
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.data.model.enum.RequestKeyStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class BlockListFragment : BaseFragment(), FeedRequestAdapterListener<FeedRequestUserData> {

    private val viewModel: BlockListViewModel by viewModels()

    private lateinit var recAdapter: BlockListAdapter

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        AllUsersFragmentBinding.inflate(layoutInflater)
    }

    override val baseViewModel: BaseViewModel
        get() = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvInboxTitle.text = resources.getString(R.string.block_list)
        binding.tvInboxCreateConversation.hide()


        viewModel.totalItemCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.FeedRecycler.show()
            } else {
                setNoData()
            }
        }

    }


    private fun setNoData() {
        binding.FeedRecycler.hide()
        binding.noDataContainer.show()
        binding.imgNoData.setImageResource(R.drawable.ic_no_data_block)
        binding.noDataFoundTxt.text = resources.getString(R.string.title_no_data_block)
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

        recAdapter = BlockListAdapter(viewModel, this)


        binding.FeedRecycler.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recAdapter.showLoading()

    }

    override fun onItemSelected(data: FeedRequestUserData) {}
    override fun onActionRequest(
        newStatus: RequestKeyStatus,
        position: Int,
        data: FeedRequestUserData,
        afterSuccessStatus: FeedKeyStatus
    ) {
        lifecycleScope.launch {
            //showLoading()
            val isSuccess = viewModel.updateUserStatus(data.userId, newStatus.key)
            //hideLoading()
            if (isSuccess) {
                viewModel.reload()
            }

        }
    }

}