package com.friendzrandroid.home.fragment.more.myProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.changeColor
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.UserProfileFragmentBinding
import com.friendzrandroid.home.adapter.ProfileImagesAdapter
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.utils.ImageDialog
import com.friendzrandroid.utils.ProfileUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MyProfileFragment : BaseFragment() {

    override val baseViewModel: BaseViewModel
        get() = viewModel

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        UserProfileFragmentBinding.inflate(layoutInflater)
    }

    private lateinit var profileImagesAdapter: ProfileImagesAdapter

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var userImage: String
    private var userAdditionalImages: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        trackScreenName("MyProfile Screen")
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.profileSwipeToRefresh.changeColor()

        viewModel.myProfileData.observe(viewLifecycleOwner) {
            binding.profileSwipeToRefresh.isRefreshing = false
            when (it) {
                is DataState.Loading -> {
                    ProfileUtil.showLoading(binding, true)
                }
                is DataState.NewData -> {
                    ProfileUtil.showLoading(binding, false)
                    setUserData(it.data)
                    Log.i("TAG Data", "onViewCreated: MyProfile ${it.data}")
                    UserSessionManagement.saveUserData(it.data)
                    //binding.profileSwipeToRefresh.isRefreshing = false
                    binding.btnProfileEdit.show()
                    binding.tvInboxTitle.text = resources.getString(R.string.title_profile)
                }
                is DataState.FailData -> {
                    ProfileUtil.showLoading(binding, false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }


        setUpSwipeToRefresh()
        goBack()
    }


    private fun setUpSwipeToRefresh() {
        binding.profileSwipeToRefresh.setOnRefreshListener {
            viewModel.getMyProfile()
        }
    }

    private var currentPageIndex = 0
    private fun setUpViewPager() {

        binding.imageSlider.adapter = profileImagesAdapter

        //set the orientation of the viewpager using ViewPager2.orientation
        binding.imageSlider.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        //select any page you want as your starting page

        binding.imageSlider.currentItem = currentPageIndex

        // registering for page change callback
        binding.imageSlider.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                }
            }
        )
    }


    private fun setUserData(data: UserProfileData) {

        userImage = data.userImage
        userAdditionalImages.add(userImage)

        if (!data.userImages.isNullOrEmpty()) {
            userAdditionalImages.addAll(data.userImages)

            binding.nextImage.show()
            binding.nextImage.setOnClickListener {
                if (currentPageIndex < userAdditionalImages.size - 1) {
                    currentPageIndex += 1
                    binding.imageSlider.currentItem = currentPageIndex
                }
            }

            binding.previousImage.show()
            binding.previousImage.setOnClickListener {
                if (currentPageIndex > 0) {
                    currentPageIndex -= 1
                    binding.imageSlider.currentItem = currentPageIndex
                }
            }

        }
        profileImagesAdapter = ProfileImagesAdapter(userAdditionalImages) {
            ImageDialog.setImageBigger(requireActivity(), it)
        }
        setUpViewPager()

//        binding.userProfileImage.loadImage(data.userImage)
        binding.profileUserName.text = data.userName
        binding.profileSystemUserName.text = "@${data.displayedUserName}"
        binding.userAgeValue.text = data.age.toString()
        binding.userGenderValue.text =
            if (data.gender.equals("other")) data.otherGenderName else data.gender
        binding.profileUserBio.text = data.bio

        context?.let {
            ProfileUtil.addChipsViews(it, binding.profileUserEnjoyTags, data.listoftagsmodel, true)

            ProfileUtil.addChipsViews(it, binding.profileUserIamTags, data.iamList, true)
            ProfileUtil.addChipsViews(it, binding.profileUserPreferTags, data.prefertoList, true)
        }

//        binding.userProfileImage.setOnClickListener {
//            ImageDialog.setImageBigger(requireActivity(), userImage)
//        }

        binding.btnProfileEdit.setOnClickListener {

            findNavController().navigate(MyProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
            //findNavController().navigate(MyProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(data))
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyProfile()
    }

    private fun goBack() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}