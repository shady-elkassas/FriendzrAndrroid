package com.friendzrandroid.home.fragment.home.events.addEvent

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
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.core.utils.Constants.DisplaytimeFormat
import com.friendzrandroid.databinding.EditEventFragmentBinding
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.TagsModel
import com.friendzrandroid.home.dialog.tagsDialog.TagDialogListener
import com.friendzrandroid.home.dialog.tagsDialog.TagsDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.util.*


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddEventFragment : BaseFragment(), TagDialogListener {

    companion object {
        var selectedAttendee: Array<String> = arrayOf()
    }

    val args: AddEventFragmentArgs by navArgs()

    var file: File? = null
    private val selectedTags = ArrayList<TagsModel>()

    val locationLang: Double by lazy { args.locationLang.toDouble() }
    val locationLat: Double by lazy { args.locationLat.toDouble() }

    lateinit var imageUtil: SelectImageUtil

    lateinit var selectedEventTypeId: String

    val binding by lazy(LazyThreadSafetyMode.NONE) {
        EditEventFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel: AddEventViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setImageUtil()
        setClicks()
        setObservers()
        initView()

        viewModel.getUserInterests()
        viewModel.getEventTypes()

        binding.closePageBtn.setOnClickListener {
            findNavController().popBackStack()
        }


        Log.e("MyLocation", "lat: ${locationLat} , long: ${locationLang}")
        return binding.root
    }

    private fun initView() {
        binding.attendeeContainerCard.hide()
        binding.shimmerLoading.hideShimmer()
        binding.appbarDeleteEvent.hide()
        binding.attendeeTitle.hide()
        binding.rvEditProfileAttendees.hide()
        binding.appbarTitle.text = resources.getString(R.string.title_addEvent)
        binding.btnEditEventSave.text = resources.getString(R.string.title_add)
    }

    private fun setImageUtil() {
        val obs = MutableLiveData<Uri>()
        imageUtil = SelectImageUtil(this, obs)

        obs.observe(viewLifecycleOwner) {
            file = File(it.path)
            binding.eventImage.loadImage(it)

        }
    }

    private fun setObservers() {
        viewModel.eventAdded.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {
                    findNavController().popBackStack()
                }

                is DataState.FailData -> {
                    showToast(it.message)
                }
                else -> {}
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it)
                binding.shimmerLoading.startShimmer()
            else
                binding.shimmerLoading.hideShimmer()
        }
    }

    private fun setClicks() {
        binding.btnEditEventSave.setOnClickListener {
            addEvent()
        }
        binding.eventCategory.setOnClickListener {
            TagsDialogFragment.instance(
                requireActivity().supportFragmentManager,
                "Event Tags",
                viewModel.allInterestList,
                selectedTags,
                1,
                this,
                true
            )
        }

        binding.addEventTimeFromValue.setOnClickListener {
            val date = Calendar.getInstance()
            viewModel.timeFrom.value?.let {
                date.time = it
            }
            TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    val results = Constants.FormatToTime(hourOfDay, minute, DisplaytimeFormat)
                    viewModel.timeFrom.value = results.first
                    binding.addEventTimeFromValue.setText(results.second)
                }
            }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
        }

        binding.addEventTimeToValue.setOnClickListener {
            val date = Calendar.getInstance()
            viewModel.timeTo.value?.let {
                date.time = it
            }
            TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    val results = Constants.FormatToTime(hourOfDay, minute, DisplaytimeFormat)
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
            showDatePickerDialog(date, Date().time, binding.addEventDateFromValue, true)
        }

        binding.addEventDateToValue.setOnClickListener {

            if (viewModel.dateFrom.value != null) {

                val date = Calendar.getInstance()
                viewModel.dateTo.value?.let {
                    date.time = it
                }

                viewModel.dateFrom.value?.let {
                    showDatePickerDialog(date, it.time, binding.addEventDateToValue, false)
                }

            } else {
                showToast(resources.getString(R.string.error_event_datefrom))
            }
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

        binding.imgEditEventCamera.setOnClickListener {
            imageUtil.selectImage(false)
        }

        binding.eventType.setOnClickListener {
            showDialog()
        }
    }

    lateinit var eventType: String
    private fun showDialog() {
        val builder = AlertDialog.Builder(activity)

        var selectedItem = -1
        val listItem: ArrayList<String> = arrayListOf()
        viewModel.eventTypes.forEachIndexed { index, item ->
            if (this::selectedEventTypeId.isInitialized && item.entityId == selectedEventTypeId)
                selectedItem = index

            listItem.add(item.name)
        }

        builder.setTitle("Select your event type")
        builder.setSingleChoiceItems(
            listItem.toTypedArray(),
            selectedItem
        ) { dialog, whichSelected ->

            selectedEventTypeId = viewModel.eventTypes[whichSelected].entityId
            eventType = viewModel.eventTypes[whichSelected].name
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

    private fun handlePrivateEvent(isPrivate: Boolean) {

        if (isPrivate) {
            binding.selectAttendeeContainer.apply {
                show()
                setOnClickListener {
                    val action =
                        AddEventFragmentDirections.actionAddEventFragmentToAddUserGroupFragment(
                            chatId = null,
                            listOfSelectedUsers = selectedAttendee
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

    private fun showDatePickerDialog(
        date: Calendar,
        minDate: Long,
        viewText: TextView,
        isFrom: Boolean
    ) {
        val x = DatePickerDialog(
            requireContext(),
            object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker?,
                    year: Int,
                    month: Int,
                    dayOfMonth: Int
                ) {
                    val results = Constants.FormatToDate(year, month, dayOfMonth)
                    if (isFrom) {
                        viewModel.dateFrom.value = results.first
                        viewText.text = results.second
                    } else {
                        viewModel.dateTo.value = results.first
                        viewText.text = results.second
                    }
                    //binding.addEventDateFromValue.setText(results.second)
                }

            },
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH)
        )
        x.datePicker.minDate = minDate
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 1)
        x.datePicker.maxDate = cal.timeInMillis

        x.show()

    }

    fun addEvent() {
        val eventTitle = binding.edtEditEventTitle.text.toString()
        if (eventTitle.isNullOrEmpty()) {
            showToast(resources.getString(R.string.error_event_title))
            return
        }
        if (selectedTags.size == 0) {
            showToast(resources.getString(R.string.error_event_category))
            return
        }
        var dateFrom = ""
        if (viewModel.dateFrom.value == null) {
            showToast(resources.getString(R.string.error_event_datefrom))
            return
        } else {
            dateFrom = viewModel.dateFrom.value!!.FormatToDate()
        }


        var dateTo = ""
        var timeFrom = ""
        var timeTo = ""
        val allDay = binding.switchAllDay.isChecked

        if (viewModel.dateTo.value == null) {
            showToast(resources.getString(R.string.error_event_dateto))
            return
        } else {
            dateTo = viewModel.dateTo.value!!.FormatToDate()
        }

        if (!allDay) {
            if (viewModel.timeFrom.value == null) {
                showToast(resources.getString(R.string.error_event_timefrom))
                return
            } else {
                timeFrom = viewModel.timeFrom.value!!.FormatToTime()
            }

            if (viewModel.timeTo.value == null) {
                showToast(resources.getString(R.string.error_event_timeto))
                return
            } else {
                timeTo = viewModel.timeTo.value!!.FormatToTime()
            }
        }


        val description = binding.edtEventDescription.text.toString()
        if (description.isEmpty()) {
            showToast(resources.getString(R.string.error_event_description))
            return
        }


        val eventNumberLimit = binding.eventLimitNumber.text.toString()
        var numberOfUsers: Int = 0

        if (eventNumberLimit.isEmpty()) {
            showToast(resources.getString(R.string.error_event_limit_number))
            return
        } else {
            try {
                numberOfUsers = eventNumberLimit.toInt()
            } catch (e: Exception) {
                showToast(resources.getString(R.string.error_event_limit_number))
                return
            }
        }

        var userIds: String = ""
        if (selectedAttendee.isNullOrEmpty() && eventType == "Private") {
            showToast(resources.getString(R.string.error_select_attendee))
        } else {

            if (binding.showAttendeeContainer.isVisible && binding.selectAttendeeContainer.isVisible) {

                userIds = selectedAttendee.joinToString(
                    prefix = "[",
                    postfix = "]",
                    separator = ",",
                    transform = { "\"$it\"" }
                )

                Log.e(
                    TAG,
                    "addEvent: Users: ${selectedAttendee.size}, Show: ${binding.attendeeShowCheckbox.isChecked}"
                )
            }
        }

        viewModel.addEvent(
            eventTitle,
            description,
            selectedTags.get(0).tagID,
            selectedEventTypeId,
            locationLat,
            locationLang,
            numberOfUsers,
            dateFrom,
            allDay,
            dateTo,
            timeFrom,
            timeTo,
            file,
            listOfUsers = if (binding.selectAttendeeContainer.isVisible) userIds else null,
            showAttendee = binding.showAttendeeContainer.isVisible
        )


    }

    override fun onSave(tageType: Int, selectedTags: ArrayList<TagsModel>) {
        setTags(selectedTags)
    }

    private fun setTags(data: List<TagsModel>) {

        selectedTags.clear()
        selectedTags.addAll(data)

        binding.ChipTags.removeAllViews()
        for (interest in selectedTags) {

            val chip = Chip(context)
            val chipDrawable: ChipDrawable = ChipDrawable.createFromAttributes(
                requireActivity(),
                null,
                0,
                R.style.My_Widget_MaterialComponents_Chip_Choice
            )
            chip.setChipDrawable(chipDrawable)
            chip.setBackgroundColor(resources.getColor(R.color.dark_chip))
            chip.setTextColor(resources.getColor(R.color.white))

            chip.setText("#${interest.tagname}")

            chip.tag = interest
            chip.setOnClickListener {
                val data = chip.tag as TagsModel

                if (!chip.isChecked) {
                    binding.ChipTags.removeView(chip)
                    selectedTags.remove(data)
                }
            }
            val c = selectedTags.find { it.tagID == interest.tagID }

            chip.isChecked = c != null

            binding.ChipTags.addView(chip)
        }

    }

    private val TAG = "AddEventFragment"
    override fun onResume() {
        super.onResume()

        Log.e(TAG, "onResume: ${selectedAttendee.size}")
    }


}