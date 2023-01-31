package com.friendzrandroid.utils

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT
import androidx.databinding.adapters.ViewBindingAdapter.setPadding
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.BaseAdapterListener
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.FragmentCommunityMemberProfileBinding
import com.friendzrandroid.databinding.UserProfileFragmentBinding
import com.friendzrandroid.home.data.model.TagsModel
import com.friendzrandroid.home.data.model.community.RecentlyConnectedItemData
import com.friendzrandroid.home.data.model.community.RecommendedPeopleResponse
import com.friendzrandroid.home.fragment.home.community.CommunityGL
import com.friendzrandroid.home.fragment.home.community.CommunityGLDirections
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup


object ProfileUtil {

    fun showLoading(binding: UserProfileFragmentBinding, show: Boolean) {
        if (show) {
            binding.ShimmerDisplayUserName.startShimmer()
            binding.ShimmerUserBio.startShimmer()
            binding.ShimmerUserImage.startShimmer()
            binding.ShimmerUserName.startShimmer()

            clearAllViewForReloading(binding)

            binding.ShimmerUserImage.show()
            binding.ShimmerTxtUserName.shimerContainer.show()
            binding.ShimmerTxtSystemUser.shimerContainer.show()
            binding.ShimmerTxtBio.shimerContainer.show()
        } else {
            binding.ShimmerDisplayUserName.hideShimmer()
            binding.ShimmerUserBio.hideShimmer()
            binding.ShimmerUserImage.hideShimmer()
            binding.ShimmerUserName.hideShimmer()
            binding.ShimmerUserImage.hide()
            binding.ShimmerTxtUserName.shimerContainer.hide()
            binding.ShimmerTxtSystemUser.shimerContainer.hide()
            binding.ShimmerTxtBio.shimerContainer.hide()
        }
    }
    fun showLoading(binding: FragmentCommunityMemberProfileBinding, show: Boolean) {
        if (show) {
            binding.ShimmerDisplayUserName.startShimmer()
            binding.ShimmerUserBio.startShimmer()
            binding.ShimmerUserImage.startShimmer()
            binding.ShimmerUserName.startShimmer()

            clearAllViewForReloading(binding)

            binding.ShimmerUserImage.show()
            binding.ShimmerTxtUserName.shimerContainer.show()
            binding.ShimmerTxtSystemUser.shimerContainer.show()
            binding.ShimmerTxtBio.shimerContainer.show()
        } else {
            binding.ShimmerDisplayUserName.hideShimmer()
            binding.ShimmerUserBio.hideShimmer()
            binding.ShimmerUserImage.hideShimmer()
            binding.ShimmerUserName.hideShimmer()
            binding.ShimmerUserImage.hide()
            binding.ShimmerTxtUserName.shimerContainer.hide()
            binding.ShimmerTxtSystemUser.shimerContainer.hide()
            binding.ShimmerTxtBio.shimerContainer.hide()
        }
    }

    fun addChipsViews(
        context: Context,
        chipView: ChipGroup,
        data: List<TagsModel>,
        myProfile: Boolean
    ) {


        chipView.removeAllViews()
        for (category in data) {

            if (!category.tagname.contains("#")) {


                val chip = Chip(context)
//            chip.setId(category.id)
                //            chip.setBackground(getContext().getDrawable(R.drawable.bg_chip_selection));
//            chip.setTextAppearance(R.style.descriptionSemiRegular)
                val chipDrawable: ChipDrawable = ChipDrawable.createFromAttributes(
                    context,
                    null,
                    0,
                    R.style.My_Widget_MaterialComponents_Chip_Choice
                )
                chip.setChipDrawable(chipDrawable)
                chip.setTextColor(context.resources.getColor(R.color.white))
                chip.textSize = context.resources.getDimension(R.dimen.d_8)
                chip.setText("#${category.tagname}")
                chip.setEnsureMinTouchTargetSize(false)
                chip.setCheckable(false)

                chipView.setChipSpacing(15)
                if (!myProfile) {
                    var single: Int = 0
                    if (UserSessionManagement.getInterestsForProfileType().size != null &&
                        UserSessionManagement.getInterestsForProfileType().size > 0
                    ) {

                        var interestsForProfileType: List<String> =
                            UserSessionManagement.getInterestsForProfileType()

                        single = interestsForProfileType.filter { s ->

                            s == category.tagID
                        }.size
                    }


//                    val c = interestsForProfileType.find { it == category.tagID }
//
//                    chip.isChecked = c != null

                    val chipDrawable: ChipDrawable = ChipDrawable.createFromAttributes(
                        context,
                        null,
                        0,
                        R.style.My_Chip
                    )


                    if (single == 1) {
//                        chip.setBackgroundColor(context.resources.getColor(R.color.dark_chip))

                        chip.setChipDrawable(chipDrawable)
                        chipView.addView(chip)
                    } else {


                        chipView.addView(chip)


                    }


                } else {
                    chipView.addView(chip)

                }


            }


        }
    }

    fun addChipsViewsForCommunity(
        context: Context,
        chipView: ChipGroup,
        data: RecommendedPeopleResponse,
    ) {
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 0f,
            context.getResources().getDisplayMetrics()
        ).toInt()


        chipView.removeAllViews()
        for (category in data.matchedInterests) {
            val chip =
                LayoutInflater.from(context).inflate(R.layout.item_community_tags, null) as Chip
            chip.setText("#${category}")
            chipView.addView(chip)

            chip.setOnClickListener {
                val userId = data.userId
                val action =
                    CommunityGLDirections.actionCommunityGLToUserProfileFragment(data.userId)

                chip.findNavController().navigate(action)

            }
        }


    }

    private fun clearAllViewForReloading(binding: UserProfileFragmentBinding) {
        binding.profileUserName.text = ""
        binding.profileSystemUserName.text = ""
        binding.profileUserBio.text = ""

        binding.userAgeValue.text = ""
        binding.userGenderValue.text = ""

        binding.profileUserEnjoyTags.removeAllViews()
        binding.profileUserIamTags.removeAllViews()
        binding.profileUserPreferTags.removeAllViews()
    }
    private fun clearAllViewForReloading(binding: FragmentCommunityMemberProfileBinding) {
        binding.profileUserName.text = ""
        binding.profileSystemUserName.text = ""
        binding.profileUserBio.text = ""

        binding.userAgeValue.text = ""
        binding.userGenderValue.text = ""

        binding.profileUserEnjoyTags.removeAllViews()
        binding.profileUserIamTags.removeAllViews()
        binding.profileUserPreferTags.removeAllViews()
    }

}