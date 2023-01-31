package com.friendzrandroid.home.dialog.filterDialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.databinding.FragmentFilterSliderBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

class FilterDialogFragment(
    private val dialogTitle: String,
    private val ageFrom: Int? = null,
    private val ageTo: Int? = null,
    private val distanceFilter: Double? = null,
    val listener: FilterDialogListener
) : BottomSheetDialogFragment() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentFilterSliderBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setSlider()

        return binding.root
    }


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        Log.e("Dialog", "onCancel: ")

        if (distanceFilter == null)
            listener.onDismissed(true)
        else
            listener.onDismissed(false)
    }

    private fun setSlider() {

        val sliderRange = UserSessionManagement.getSettingConfiguration()
        Log.e("TAG", "setSlider: $sliderRange")

        binding.dialogTitle.text = dialogTitle

        if (distanceFilter == null) { // We now see Age Slider

            binding.dialogDistanceSlider.visibility = View.GONE


            val ageF =
                if (ageFrom!! < sliderRange.ageFiltering_Min || ageFrom == 0) sliderRange.ageFiltering_Min
                else ageFrom

            val ageT =
                if (ageTo!! > sliderRange.ageFiltering_Max || ageTo < sliderRange.ageFiltering_Min || ageTo == 0) sliderRange.ageFiltering_Max
                else ageTo

            binding.dialogAgeSlider.apply {
                valueFrom = sliderRange.ageFiltering_Min.toFloat()
                valueTo = sliderRange.ageFiltering_Max.toFloat()
                values = listOf(
                    ageF.toFloat(),
                    ageT.toFloat()
                )
            }

            binding.textValue.hide()
            binding.textValueFrom.text = ageF.toString()
            binding.textValueTo.text = ageT.toString()

            binding.dialogAgeSlider.labelBehavior = LabelFormatter.LABEL_GONE
            binding.dialogAgeSlider.addOnSliderTouchListener(object :
                RangeSlider.OnSliderTouchListener {
                @SuppressLint("RestrictedApi")
                override fun onStartTrackingTouch(slider: RangeSlider) {
                    //binding.textValueFrom.show()
                    binding.textValueFrom.text = slider.values[0].roundToInt().toString()

                    //binding.textValueTo.show()
                    binding.textValueTo.text = slider.values[1].roundToInt().toString()
                }

                @SuppressLint("RestrictedApi")
                override fun onStopTrackingTouch(slider: RangeSlider) {
                    //binding.textValueFrom.show()
                    binding.textValueFrom.text = slider.values[0].roundToInt().toString()

                    //binding.textValueTo.show()
                    binding.textValueTo.text = slider.values[1].roundToInt().toString()
                }

            })

        } else { // We now see distance Slider

            binding.dialogAgeSlider.visibility = View.GONE

            val sliderDistanceValue =
                when {
                    distanceFilter.roundToInt().toFloat() < sliderRange.distanceFiltering_Min -> sliderRange.distanceFiltering_Min
                    distanceFilter.roundToInt().toFloat() > sliderRange.distanceFiltering_Max -> sliderRange.distanceFiltering_Max
                    else -> distanceFilter
                }


            binding.dialogDistanceSlider.apply {
                valueFrom = sliderRange.distanceFiltering_Min.toFloat()
                valueTo = sliderRange.distanceFiltering_Max.toFloat()
                value = sliderDistanceValue.toFloat()
            }
            Handler().post(Runnable { updateValueLabelPosition(binding.dialogDistanceSlider) })

            binding.textValueTo.hide()
            binding.textValueFrom.hide()

            updateValueLabelPosition(binding.dialogDistanceSlider)
            binding.textValue.text = distanceFilter.roundToInt().toString()+"km"

            binding.dialogDistanceSlider.addOnChangeListener { slider, value, fromUser ->
                //binding.textValue.show()
                binding.textValue.text = value.roundToInt().toString() +"km"

                Handler().post(Runnable { updateValueLabelPosition(slider) })
            }
        }

        binding.dialogSaveButton.setOnClickListener {
            listener.onSave(
                ageFrom = binding.dialogAgeSlider.values.first().toInt(),
                ageTo = binding.dialogAgeSlider.values.last().toInt(),
                distance = binding.dialogDistanceSlider.value.toDouble(),
                isAgeSlider = binding.dialogAgeSlider.isVisible
            )
            dismiss()
        }
    }

    private fun updateValueLabelPosition(slider: Slider) {
        val valueRangeSize: Float = slider.valueTo - slider.valueFrom
        val valuePercent: Float = (slider.value - slider.valueFrom) / valueRangeSize
        val valueXDistance: Float = valuePercent * slider.trackWidth
        val offset: Float =
            slider.x + slider.trackSidePadding - binding.textValue.width.toFloat() / 2

        binding.textValue.x = valueXDistance + offset
    }

    companion object {
        fun instance(
            fragmentManager: FragmentManager,
            dialogTitle: String,
            ageFrom: Int? = null,
            ageTo: Int? = null,
            distanceFilter: Double? = null,
            listener: FilterDialogListener
        ) {
            val dialog =
                FilterDialogFragment(
                    dialogTitle,
                    ageFrom,
                    ageTo,
                    distanceFilter,
                    listener
                )
            dialog.setStyle(
                STYLE_NO_TITLE,
                R.style.AppBottomSheetDialogTheme
            )
            dialog.show(fragmentManager, "filterSliderDialog")
        }
    }
}