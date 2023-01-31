package com.friendzrandroid.home.fragment.home.events.eventList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.core.utils.changeColor
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.EventsFragmentBinding
import com.friendzrandroid.home.adapter.listener.EventAdapterListener
import com.friendzrandroid.home.adapter.EventsAdapter
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.utils.AdsBannerUtil
import com.friendzrandroid.utils.ItemMarginBottomDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EventsFragment : Fragment(), EventAdapterListener<EventItemData> {

    //R.layout.events_fragment

    private val viewModel: EventsViewModel by viewModels()


    private val binding: EventsFragmentBinding by lazy(LazyThreadSafetyMode.NONE) {
        EventsFragmentBinding.inflate(layoutInflater)
    }

    lateinit var eventAdapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        handleSwip()

        binding.swipeToRefresh.changeColor()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalItemCount.observe(viewLifecycleOwner) {

            binding.swipeToRefresh.isRefreshing = false
            if (it > 0) {
                binding.eventRec.visibility=View.VISIBLE
                binding.noDataContainer.visibility=View.GONE
            } else {
                binding.eventRec.visibility=View.GONE
                binding.noDataContainer.visibility=View.VISIBLE
            }
            binding.allEventCount.text = "${it}"
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        AdsBannerUtil.loadAds(binding.bannerAdView)

    }


    fun handleSwip() {
        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.reload()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!this::eventAdapter.isInitialized) {

            eventAdapter = EventsAdapter(viewModel, this)
            val bottomItemDecoration = ItemMarginBottomDecoration()
            binding.eventRec.addItemDecoration(bottomItemDecoration)


            binding.eventRec.apply {
                adapter = eventAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            eventAdapter.showLoading()

        } else
            viewModel.reload()
    }

    override fun onItemSelected(data: EventItemData) {
        if (data != null)
            findNavController().navigate(
                EventsFragmentDirections.actionEventsFragmentToEventDetailsFragment(data.id)
            )
    }

    override fun onItemEditButtonPressed(data: EventItemData) {
        val direction = EventsFragmentDirections.actionEventsFragmentToEditEventFragment(data.id)
        findNavController().navigate(direction)
        //Toast.makeText(requireContext(), "Editbutton Pressed", Toast.LENGTH_SHORT).show()
    }

}