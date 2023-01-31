package com.friendzrandroid.home.fragment.home.genderDistribution

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.futured.donut.DonutSection
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.FragmentEventAttendanceBinding
import com.friendzrandroid.databinding.FragmentGenderDistributionBinding
import com.friendzrandroid.home.MainViewModel
import com.friendzrandroid.home.data.model.GenderStatistic
import com.friendzrandroid.home.data.model.InterestStatistic
import com.friendzrandroid.home.fragment.home.map.MapsFragmentDirections
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class GenderDistributionFragment : Fragment() {

    val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentGenderDistributionBinding.inflate(layoutInflater)
    }

    val args: GenderDistributionFragmentArgs by navArgs()
    val genderDistData by lazy {
        args.genderDist
    }

    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.addNewEvent.setOnClickListener {
            val action =
                GenderDistributionFragmentDirections.actionNavigationToAddEvent(
                    locationLat = genderDistData.lat.toString(),
                    locationLang = genderDistData.lang.toString()
                )
            findNavController().navigate(action)
        }
        setChartData(
            genderDistData.malePercentage,
            genderDistData.femalepercentage,
            genderDistData.otherpercentage,
            genderDistData.totalUsers
        )
        setGenderData(
            genderDistData.malePercentage,
            genderDistData.femalepercentage,
            genderDistData.otherpercentage
        )
        return binding.root
    }


    private fun setChartData(male: Double, female: Double, other: Double, joind: Int) {

        val items = ArrayList<DonutSection>()
        val maleChart = DonutSection(
            name = "male",
            color = ContextCompat.getColor(requireContext(), R.color.color_male),
            amount = male.toFloat()
        )

        val femaleChart = DonutSection(
            name = "female",
            color = ContextCompat.getColor(requireContext(), R.color.color_female),
            amount = female.toFloat()
        )

        val otherChart = DonutSection(
            name = "other",
            color = ContextCompat.getColor(requireContext(), R.color.color_other),
            amount = other.toFloat()
        )
        items.add(maleChart)
        items.add(femaleChart)
        items.add(otherChart)

        binding.eventInterestsChart.cap = joind.toFloat()
        try {
            binding.eventInterestsChart.submitData(items)
        } catch (e: Exception) {

        }
    }

    private fun setGenderData(male: Double, female: Double, other: Double) {
        binding.malePercentValue.text = "${male}%"
        binding.femalePercentValue.text = "${female}%"
        binding.otherPercentValue.text = "${other}%"

    }

}