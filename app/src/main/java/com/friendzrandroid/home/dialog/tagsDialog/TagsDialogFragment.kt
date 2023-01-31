package com.friendzrandroid.home.dialog.tagsDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.showToast
import com.friendzrandroid.databinding.FragmentTagsDialogBinding
import com.friendzrandroid.home.data.model.InterestData
import com.friendzrandroid.home.data.model.TagsModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

class TagsDialogFragment(
    val title: String,
    val allTags: ArrayList<InterestData>,
    val selectedTags: ArrayList<TagsModel>,
    val tagType: Int,
    val listener: TagDialogListener,
    val isSingleSelection: Boolean
) :
    BottomSheetDialogFragment() {

    private val updateSelectedTags = ArrayList<TagsModel>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentTagsDialogBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        updateSelectedTags.clear()
        updateSelectedTags.addAll(selectedTags)
        addChipsViews()
        binding.dialogTitle.text = title

        binding.saveBtn.setOnClickListener {
            dismiss()
            listener.onSave(tagType, updateSelectedTags)
        }

        return binding.root
    }

    private fun addChipsViews() {
        binding.ChipTags.isSingleSelection = isSingleSelection
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

                chip.setText("#${interest.name}")

                chip.tag = interest
                chip.setOnClickListener {

                    if (!isSingleSelection) {
                        val data = chip.tag as InterestData
                        val tag = TagsModel(data.id, data.name)

                        if (chip.isChecked) {

                            when (tagType) {
                                1 -> if (updateSelectedTags.size < 8) {
                                    updateSelectedTags.add(tag)
                                } else {
                                    Toast(requireContext()).showToast(
                                        activity,
                                        resources.getString(R.string.max_tags_numbers_error)
                                    )
                                    chip.isChecked = false
                                }

                                2, 3 -> if (updateSelectedTags.size < 4) {
                                    updateSelectedTags.add(tag)
                                } else {
                                    Toast(requireContext()).showToast(
                                        activity,
                                        resources.getString(R.string.max_tags_four_numbers_error)
                                    )
                                    chip.isChecked = false
                                }
                            }


                        } else {
                            updateSelectedTags.remove(tag)
                        }
                    } else {
                        updateSelectedTags.clear()
                        if (chip.isChecked) {
                            val data = chip.tag as InterestData
                            val tag = TagsModel(data.id, data.name)
                            updateSelectedTags.add(tag)
                        }
                    }


                }
                val c = selectedTags.find { it.tagID == interest.id }

                chip.isChecked = c != null

                binding.ChipTags.addView(chip)


            }}
        }

        companion object {

            fun instance(
                fragmentManager: FragmentManager,
                title: String,
                allTags: ArrayList<InterestData>,
                selectedTags: ArrayList<TagsModel>,
                tagType: Int,
                lisener: TagDialogListener,
                isSingleSelection: Boolean = false
            ) {
                val dialog =
                    TagsDialogFragment(
                        title,
                        allTags,
                        selectedTags,
                        tagType,
                        lisener,
                        isSingleSelection
                    )
                dialog.setStyle(
                    STYLE_NO_TITLE,
                    R.style.AppBottomSheetDialogTheme
                )
                dialog.show(fragmentManager, "interestsDialog")
            }

        }
    }