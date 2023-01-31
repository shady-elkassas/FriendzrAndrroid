package com.friendzrandroid.home.dialog.eventsDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendzrandroid.R
import com.friendzrandroid.databinding.FragmentEventsDialogBinding
import com.friendzrandroid.databinding.FragmentTagsDialogBinding
import com.friendzrandroid.home.data.model.EventMapData
import com.friendzrandroid.home.data.model.EventMapResponseItem
import com.friendzrandroid.home.data.model.InterestData
import com.friendzrandroid.home.data.model.TagsModel
import com.friendzrandroid.home.dialog.tagsDialog.TagDialogListener
import com.friendzrandroid.home.dialog.tagsDialog.TagsDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EventsDialog(val data: List<EventMapData>, val listener:onEventSelected):BottomSheetDialogFragment() {


    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentEventsDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.eventsRec.apply {
            layoutManager = LinearLayoutManager(requireContext())

            adapter = EventsDialogAdapter(this@EventsDialog,data,listener)
        }

        return binding.root
    }


    companion object {

        fun instance(
            fragmentManager: FragmentManager,
             data: List<EventMapData>,  listener:onEventSelected
        ) {
            val dialog = EventsDialog(data,listener)
            dialog.setStyle(
                STYLE_NO_TITLE,
                R.style.AppBottomSheetDialogTheme
            )
            dialog.show(fragmentManager, "eventssDialog")
        }

    }
}