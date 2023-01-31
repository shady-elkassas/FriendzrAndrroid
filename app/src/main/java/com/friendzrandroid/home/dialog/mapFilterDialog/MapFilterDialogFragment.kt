package com.friendzrandroid.home.dialog.mapFilterDialog

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.applovin.impl.sdk.utils.Utils.showToast
import com.facebook.appevents.codeless.internal.ViewHierarchy.setOnClickListener
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.FragmentMapFilterTagsDialogBinding
import com.friendzrandroid.databinding.FragmentTagsDialogBinding
import com.friendzrandroid.home.data.model.InterestData
import com.friendzrandroid.home.data.model.TagsModel
import com.friendzrandroid.home.dialog.tagsDialog.TagDialogListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import java.util.*
import kotlin.collections.ArrayList

class MapFilterDialogFragment(
    val title: String,
    val allTags: ArrayList<InterestData>,
    val selectedTags: ArrayList<TagsModel>,
    val listener: MapFilterDialogListener
) :
    BottomSheetDialogFragment() {

    private val updateSelectedTags = ArrayList<TagsModel>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentMapFilterTagsDialogBinding.inflate(layoutInflater)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        listener.onMapFilterDismiss(true)


    }

    override fun onDestroy() {
        super.onDestroy()
        listener.onMapFilterDismiss(true)


    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onMapFilterDismiss(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        updateSelectedTags.clear()
        updateSelectedTags.addAll(selectedTags)
        addChipsViews()
//        binding.dialogTitle.text = title

        binding.saveBtn.setOnClickListener {
            listener.onMapFilterSave(updateSelectedTags)
            dismiss()

        }
//        binding.rbCustom.setOnClickListener {
//
//
//        }

        binding.rbCustom.setOnCheckedChangeListener { buttonView, isChecked ->


            if (isChecked){
                binding.rbDateStart.show()
                binding.rbDateEnd.show()

            }else{
                binding.rbDateStart.visibility=View.INVISIBLE
                binding.rbDateStart.text=""
                binding.rbDateEnd.visibility=View.INVISIBLE
                binding.rbDateEnd.text=""

            }
        }
        binding.rbDateStart.setOnClickListener {
            val date = Calendar.getInstance()

            binding.rbDateStart.show()
            showDatePickerDialog(date, Date().time, binding.rbDateStart, true)

        }

        binding.rbDateEnd.setOnClickListener {
            binding.rbDateEnd.show()

            if (binding.rbDateStart.text != null) {

                val date = Calendar.getInstance()

                showDatePickerDialog(date, date.time.time, binding.rbDateEnd, false)



            } else {
                Toast(requireContext()).showToast(activity, resources.getString(R.string.error_event_datefrom))

            }
        }



        return binding.root
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
//                        viewModel.dateFrom.value = results.first
                        viewText.text = results.second
                    } else {
//                        viewModel.dateTo.value = results.first
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

    private fun addChipsViews() {
        binding.ChipTags.removeAllViews()
        for (interest in allTags) {
            if (interest.name.contains("#")) {

            } else {
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

                chip.setText(interest.name)

                chip.tag = interest
                chip.setOnClickListener {


                    val data = chip.tag as InterestData
                    val tag = TagsModel(data.id, data.name)

                    if (chip.isChecked) {

                        updateSelectedTags.add(tag)


                    } else {
                        updateSelectedTags.remove(tag)
                    }


                }
                val c = selectedTags.find { it.tagID == interest.id }

                chip.isChecked = c != null

                binding.ChipTags.addView(chip)


            }
        }
    }

    companion object {

        fun instance(
            fragmentManager: FragmentManager,
            title: String,
            allTags: ArrayList<InterestData>,
            selectedTags: ArrayList<TagsModel>,
            lisener: MapFilterDialogListener,
        ) {
            val dialog =
                MapFilterDialogFragment(
                    title,
                    allTags,
                    selectedTags,
                    lisener,

                    )
            dialog.setStyle(
                STYLE_NO_TITLE,
                R.style.AppBottomSheetDialogTheme
            )
            dialog.show(fragmentManager, "Filter events by category")
        }

    }
}