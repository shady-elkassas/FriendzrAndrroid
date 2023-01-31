package com.friendzrandroid.home.fragment.home.events.eventAttendances

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.FragmentEventAttendanceBinding
import com.friendzrandroid.home.adapter.EventAttendaceAdapter
import com.friendzrandroid.home.data.model.AttendenceData
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.enum.CickUserFromEventEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EventAttendancesFragment : BaseFragment(), EventAttendaceAdapter.EventAttendanceCallBack {

    val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentEventAttendanceBinding.inflate(layoutInflater)
    }
    private val viewModel: EventAttendanceViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel


    val args: EventAttendancesFragmentArgs by navArgs()
    val eventID by lazy {
        Log.e("eventDetails", args.eventID)
        args.eventID
    }
    val isAdmin by lazy { args.isEventAdmin }

    lateinit var recAdapter:EventAttendaceAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initRecycler()
        viewModel.getEventDetails(eventID)

        viewModel.acctionHappend.observe(viewLifecycleOwner) {
            when (it) {
                CickUserFromEventEnum.REMOVE.value -> {
                    showToast("Account is removed")
                }

                CickUserFromEventEnum.BLOCK.value -> {
                    showToast("Account is blocked")

                }
            }
        }

        viewModel.eventAttendanceList.observe(viewLifecycleOwner) {

            when (it) {
                is DataState.NewData -> {
                    if (it.data.data.isEmpty()) {
                        binding.recAtten.hide()
                        binding.noData.show()
                    } else {
                        binding.noData.hide()
                        binding.recAtten.show()
                        recAdapter.setList(it.data.data)
                    }
                }
                is DataState.FailData -> {
                    showToast(it.message)
                }
                else -> {}
            }

        }


        return binding.root
    }

    private fun initRecycler(){
        recAdapter = EventAttendaceAdapter(this,true, isEventAdmin = isAdmin)
        binding.recAtten.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recAdapter
        }
        recAdapter.showLoading()
    }

    override fun onActionTapper(btnView: View, eventAttend: AttendenceData) {
        val popupMenu = PopupMenu(requireActivity(), btnView, Gravity.BOTTOM)

        popupMenu.menuInflater.inflate(R.menu.user_event_actions, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.remove -> {
                    removeUser(eventAttend.userId)
                }
                R.id.block -> {
                    blockUser(eventAttend.userId)

                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }


    private fun removeUser(userID: String) {
        viewModel.cickUser(eventID, userID, CickUserFromEventEnum.REMOVE)

    }

    private fun blockUser(userID: String) {
        viewModel.cickUser(eventID, userID, CickUserFromEventEnum.BLOCK)

    }

}