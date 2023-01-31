package com.friendzrandroid.home.fragment.home.events.editEvents

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.EditEventFragmentBinding
import com.friendzrandroid.home.adapter.EventAttendaceAdapter
import com.friendzrandroid.home.data.model.AttendenceData
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.EventDetails
import com.friendzrandroid.home.data.model.EventTypesResponse
import com.friendzrandroid.home.data.model.enum.CickUserFromEventEnum
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.fragment.home.events.eventDetails.EventDetailsFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditEventFragment : BaseFragment(), EventAttendaceAdapter.EventAttendanceCallBack {

    private val TAG = "EditEventFragment"

    companion object {
        var selectedAttendee: ArrayList<String> = arrayListOf()
        var firstStart: Boolean = true
    }

    val args: EventDetailsFragmentArgs by navArgs()

    val eventID by lazy {
        Log.e("eventDetails", args.eventID)
        args.eventID
    }

    lateinit var selectedEventTypeId: String
    var eventTypesList: ArrayList<EventTypesResponse> = arrayListOf()

    val binding by lazy(LazyThreadSafetyMode.NONE) {
        EditEventFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel: EditEventViewModel by viewModels()


    override val baseViewModel: BaseViewModel
        get() = viewModel


    lateinit var imageUtil: SelectImageUtil
    var file: File? = null

    lateinit var recAdapter: EventAttendaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //binding.shimmerLoading.hideShimmer()
        binding.categoryContainer.hide()

        setImageUtil()
        initClicks()
        initObs()
        initRecycler()

        viewModel.getEventDetails(eventID)
        viewModel.getEventTypes()
        viewModel.getEventAttendee(eventID)

        return binding.root
    }

    private fun setImageUtil() {
        val obs = MutableLiveData<Uri>()
        imageUtil = SelectImageUtil(this, obs)

        obs.observe(viewLifecycleOwner) {
            file = File(it.path)
            binding.eventImage.loadImage(it)

        }
    }

    fun initObs() {

        viewModel.eventAttendanceList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    if (firstStart) {
                        selectedAttendee.clear()
                        for (id in it.data.data)
                            selectedAttendee.add(id.userId)

                        selectedAttendee.removeAt(0)
                    }
                }
                else -> {}
            }
            Log.e(TAG, "initObs: $selectedAttendee")
        }

        viewModel.eventTypes.observe(viewLifecycleOwner) {
            Log.i(TAG, "initObs: Event Types: $it")
            eventTypesList.addAll(it)
        }

        viewModel.deleteEvent.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().popBackStack()
            }
        }


        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.shimmerLoading.startShimmer()
                //showLoading()
            } else {
                binding.shimmerLoading.hideShimmer()
                //hideLoading()
            }
        }

        viewModel.eventUpdated.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    showToast("event has been updated successfully")
                    binding.btnEditEventSave.hideButtonLoading(
                        resources.getString(R.string.title_updated),
                        requireActivity()
                    ) {
                        findNavController().popBackStack()
                    }
                }
                is DataState.FailData -> {
                    binding.btnEditEventSave.showButtonLoading(resources.getString(R.string.title_save))
                    showToast(it.message)
                }
                else -> {}
            }
        }

        viewModel.eventDetails.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    binding.shimmerLoading.startShimmer()

                }
                is DataState.NewData -> {
                    binding.shimmerLoading.hideShimmer()

                    setEventData(it.data)
                }
                else -> {}
            }
        }
    }

    private fun initClicks() {




        binding.seeMoreContianer.setOnClickListener {
            val action =
                EditEventFragmentDirections.actionEditEventFragmentToEventAttendenceFragment(eventID)
            findNavController().navigate(action)
        }

        ldeleteEvent()
        binding.imgEditEventCamera.setOnClickListener {
            imageUtil.selectImage(false)

        }
        binding.closePageBtn.setOnClickListener { findNavController().popBackStack() }

        binding.addEventTimeFromValue.setOnClickListener {
            val date = Calendar.getInstance()
            viewModel.timeFrom.value?.let {
                date.time = it
            }
            TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    val results = Constants.FormatToTime(hourOfDay, minute)
                    viewModel.timeFrom.value = results.first
                    binding.addEventTimeFromValue.setText(results.second)
                }
            }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
        }
        binding.btnEditEventSave.setOnClickListener {
            editEvent()
        }
        binding.addEventTimeToValue.setOnClickListener {
            val date = Calendar.getInstance()
            viewModel.timeTo.value?.let {
                date.time = it
            }
            TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    val results = Constants.FormatToTime(hourOfDay, minute)
                    viewModel.timeTo.value = results.first
                    binding.addEventTimeToValue.setText(results.second)
                }
            }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
        }
        binding.addEventDateFromValue.setOnClickListener {
            val date = Calendar.getInstance()
            viewModel.dateFrom.value?.let {
                date.time = it
            }
            DatePickerDialog(
                requireContext(),
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        val results = Constants.FormatToDate(year, month, dayOfMonth)
                        viewModel.dateFrom.value = results.first
                        binding.addEventDateFromValue.setText(results.second)
                    }

                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            ).show()


        }
        binding.addEventDateToValue.setOnClickListener {
            val date = Calendar.getInstance()
            viewModel.dateTo.value?.let {
                date.time = it
            }
            DatePickerDialog(
                requireContext(),
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        val results = Constants.FormatToDate(year, month, dayOfMonth)
                        viewModel.dateTo.value = results.first
                        binding.addEventDateToValue.setText(results.second)
                    }

                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            ).show()


        }

        binding.switchAllDay.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                binding.addEventTimeFromTitle.hide()
                binding.addEventTimeFromValue.hide()
                binding.addEventTimeToTitle.hide()
                binding.addEventTimeToValue.hide()
            } else {
                binding.addEventTimeFromTitle.show()
                binding.addEventTimeFromValue.show()
                binding.addEventTimeToTitle.show()
                binding.addEventTimeToValue.show()
            }

        }

        binding.eventType.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(activity)

        var selectedItem = -1
        val listItem: ArrayList<String> = arrayListOf()
        eventTypesList.forEachIndexed { index, item ->
            if (item.entityId == selectedEventTypeId)
                selectedItem = index

            listItem.add(item.name)
        }

        builder.setTitle("Select your event type")
        builder.setSingleChoiceItems(
            listItem.toTypedArray(),
            selectedItem
        ) { dialog, whichSelected ->

            selectedEventTypeId = eventTypesList[whichSelected].entityId
            val eventType = eventTypesList[whichSelected].name
            binding.eventType.text = eventType

            if (eventType == "Private")
                handlePrivateEvent(true)
            else
                handlePrivateEvent(false)

            // when selected an item the dialog should be closed with the dismiss method
            dialog.dismiss()
        }

        builder.create()
        builder.show()

    }


    private fun setEventData(data: EventDetails) {


        selectedEventTypeId = data.eventtypeid

//        if (firstStart) {
//            selectedAttendee.clear()
//            for (id in data.attendees)
//                selectedAttendee.add(id.userId)
//
//            selectedAttendee.removeAt(0)
//        }

        if (data.eventTypeName == "Private")
            handlePrivateEvent(true)
        else
            handlePrivateEvent(false)


        binding.eventImage.loadImage(data.image)
        binding.edtEditEventTitle.setText(data.title)
        binding.eventCategory.text =
            "${resources.getString(R.string.category)}: ${data.categorie}"
        binding.switchAllDay.isChecked = data.allday

        binding.addEventDateFromValue.text = data.eventdate
        binding.addEventDateToValue.text = data.eventdateto

        if (!data.allday){
            binding.addEventTimeFromValue.text = data.timefrom
            binding.addEventTimeToValue.text = data.timeto
        }





        binding.edtEventDescription.setText(data.description)
        binding.eventLimitNumber.setText(data.totalnumbert.toString())
        binding.eventType.text = data.eventTypeName
        recAdapter.setList(data.attendees)


    }

    private fun handlePrivateEvent(isPrivate: Boolean) {

        if (isPrivate) {
            binding.selectAttendeeContainer.apply {
                show()
                setOnClickListener {
                    val action =
                        EditEventFragmentDirections.actionEditEventFragmentToAddUserGroupFragment(
                            chatId = null,
                            listOfSelectedUsers = selectedAttendee.toTypedArray(),
                            isEditEvent = true
                        )

                    findNavController().navigate(action)
                }
            }


            binding.showAttendeeContainer.show()

        } else {
            binding.selectAttendeeContainer.hide()
            binding.showAttendeeContainer.hide()
        }
    }

    private fun initRecycler() {
        recAdapter = EventAttendaceAdapter(this, true)
        binding.rvEditProfileAttendees.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recAdapter
        }
        recAdapter.showLoading()
    }


    fun editEvent() {

        val eventTitle = binding.edtEditEventTitle.text.toString()
        if (eventTitle.isNullOrEmpty()) {
            showToast(R.string.error_event_title)
            return
        }

        var dateFrom = ""
        if (viewModel.dateFrom.value == null) {
            dateFrom = binding.addEventDateFromValue.text.toString()

        } else {
            dateFrom = viewModel.dateFrom.value!!.FormatToDate()
        }

        var timeFrom = ""
        if (viewModel.timeFrom.value == null) {
            timeFrom = binding.addEventTimeFromValue.text.toString()

        } else {
            timeFrom = viewModel.timeFrom.value!!.FormatToTime()
        }

        var dateTo = ""
        var timeTo = ""
        val allDay = binding.switchAllDay.isChecked

        if (viewModel.dateTo.value == null) {
            dateTo = binding.addEventDateToValue.text.toString()
        } else {
            dateTo = viewModel.dateTo.value!!.FormatToDate()
        }

        if (viewModel.timeTo.value == null) {

            timeTo = binding.addEventTimeToValue.text.toString()
        } else {
            timeTo = viewModel.timeTo.value!!.FormatToTime()
        }


        val description = binding.edtEventDescription.text.toString()
        if (description.isEmpty()) {
            showToast(R.string.error_event_description)
            return
        }


        val eventNumberLimit = binding.eventLimitNumber.text.toString()
        var numberOfUsers: Int = 0

        if (eventNumberLimit.isEmpty()) {
            showToast(R.string.error_event_limit_number)
            return
        } else {
            try {
                numberOfUsers = eventNumberLimit.toInt()
            } catch (e: Exception) {
                showToast(R.string.error_event_limit_number)
                return
            }
        }

        var userIds: String = ""
        if (binding.showAttendeeContainer.isVisible && binding.selectAttendeeContainer.isVisible) {

            userIds = selectedAttendee.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ",",
                transform = { "\"$it\"" }
            )

            Log.e(
                TAG,
                "editEvent: Users: ${selectedAttendee.size}, Show: ${binding.attendeeShowCheckbox.isChecked}"
            )
        }

        binding.btnEditEventSave.showButtonLoading(resources.getString(R.string.title_saving))

        viewModel.editEvent(
            eventID,
            eventTitle,
            description,
            numberOfUsers,
            dateFrom,
            allDay,
            dateTo,
            timeFrom,
            timeTo,
            file,
            selectedEventTypeId,
            listOfUsers = if (binding.selectAttendeeContainer.isVisible) userIds else null,
            showAttendee = binding.showAttendeeContainer.isVisible
        )


    }

    override fun onActionTapper(btnView: View, eventAttend: AttendenceData) {
        val popupMenu = PopupMenu(requireActivity(), btnView)

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
        viewModel.cickUser(eventID, userID, CickUserFromEventEnum.REMOVE)

    }


    private fun ldeleteEvent() {
        binding.appbarDeleteEvent.setOnClickListener {
            ConfirmationDialog(
                requireContext(),
                getString(R.string.delete_event_message),
                true
            ).showDialog {
                if (it == 1)
                    viewModel.deleteEvent(eventID)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.e(TAG, "onResume: Size: ${selectedAttendee.size}, List: $selectedAttendee")

    }

}