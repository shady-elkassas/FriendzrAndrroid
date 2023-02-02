package com.friendzrandroid.home.fragment.more.editProfile

import android.Manifest.permission.*
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.EditProfileFragmentBinding
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.TagsModel
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.data.model.enum.GenderEnum
import com.friendzrandroid.home.data.model.enum.NeedToUpdateStatus
import com.friendzrandroid.home.data.model.enum.ProfileTagsEnum
import com.friendzrandroid.home.dialog.AddImagesDialogListener
import com.friendzrandroid.home.dialog.AdditionalImagesDialog
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.dialog.ImageVerificationDialog
import com.friendzrandroid.home.dialog.tagsDialog.TagDialogListener
import com.friendzrandroid.home.dialog.tagsDialog.TagsDialogFragment
import com.friendzrandroid.home.fragment.more.myProfile.MyProfileFragmentDirections
import com.friendzrandroid.splash.presentation.activity.IntroActivity
import com.friendzrandroid.utils.ProfileUtil
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditProfileFragment : BaseFragment(), TagDialogListener, ImagePickerResultListener,
    AddImagesDialogListener {

    private val TAG = "EditProfileFragment"

    private val selectedInterestsTags = ArrayList<TagsModel>()
    private val selectedIamTags = ArrayList<TagsModel>()
    private val selectedPrefersTags = ArrayList<TagsModel>()

    private val viewModel: EditProfileViewModel by viewModels()
    var file: File? = null
    lateinit var imageUtil: SelectImageUtil
    lateinit var imageVerificationUtil: SelectImageUtil

    var firstImageFile: String? = null
    var secondImageFile: String? = null
    var RECORD_REQUEST_CODE: Int = 0

    //private var isImageUpdate: Boolean = false

    private val imagePicker: ImagePicker by lazy {
        registerImagePicker(this)
    }
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        EditProfileFragmentBinding.inflate(layoutInflater)
    }
    override val baseViewModel: BaseViewModel
        get() = viewModel

    private var isSavingNewData = false

    private var additionalImages: List<String> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.getMyProfile()

        binding.imgEditProfileBack.setOnClickListener { findNavController().popBackStack() }

        val obs = MutableLiveData<Uri>()
        imageUtil = SelectImageUtil(this, obs)

        obs.observe(viewLifecycleOwner) {
            file = File(it.path)
            firstImageFile = it.path
            verifySelectedImage()

            //Log.e(TAG, "onCreateView: Image: ${it.path}")
            //viewModel.comparingFace(it.path!!, it.path!!)
            //binding.imgUserProfile.loadImage(it)

        }


        val obsVerification = MutableLiveData<Uri>()
        imageVerificationUtil = SelectImageUtil(this, obsVerification)

        obsVerification.observe(viewLifecycleOwner) {
            secondImageFile = it.path
            isProcessing.postValue(true)
            viewModel.getAwsCredentials()
            //doVerification()
        }

        setObservers()
        setClicks()
//        checkDeepLink()

        return binding.root
    }

    private fun checkDeepLink(data: List<TagsModel>) {
        val selectedInterestsTags = ArrayList<TagsModel>()

        val bundle = this.arguments
        if (bundle != null) {
            var directionId = bundle.getString("directionId")

//            val bottomNavigationView =
//                requireActivity().findViewById<BottomNavigationView>(com.friendzrandroid.R.id.nav_view)
//            bottomNavigationView.visibility = View.GONE

            val editProfileFragment = this

            lifecycleScope.launch {

                delay(800)
                if (directionId.equals("interests")) {

                    viewModel.getUserInterests()

                    selectedInterestsTags.addAll(data)

                    TagsDialogFragment.instance(
                        requireActivity().supportFragmentManager,
                        "I enjoy...",
                        viewModel.allInterestList,
                        selectedInterestsTags,
                        1,
                        editProfileFragment
                    )


                }

            }


        }
    }

    private fun verifySelectedImage() {
        ImageVerificationDialog(requireContext(), firstImageFile!!).showDialog {
            if (it == 1) {
                imageVerificationUtil.openCamera(true)
            }
        }
    }

    private fun doVerification(accessKey: String, secretKey: String) {
        viewModel.comparingFace(
            requireActivity(),
            accessKey,
            secretKey,
            firstImageFile!!,
            secondImageFile!!
        )
    }

    private val isProcessing: MutableLiveData<Boolean> = MutableLiveData()

    private fun setObservers() {

        isProcessing.observe(viewLifecycleOwner) {
            if (it) {
                binding.changeImageStateText.show()
                binding.changeImageStateText.text = resources.getString(R.string.title_processing)
            }
        }

        viewModel.awsData.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> {

                    if (it.data.androidFrAccessKey != null && it.data.androidFrSecretKey != null)
                        doVerification(
                            accessKey = it.data.androidFrAccessKey,
                            secretKey = it.data.androidFrSecretKey
                        )

                }

                is DataState.FailData -> {
                    showToast(it.message)
                }
                else -> {}
            }
        }

        viewModel.faceComparingResult.observe(viewLifecycleOwner) {
            Log.e(TAG, "setObservers: $it")
            if (it.faceMatches.isNotEmpty()) {
                binding.changeImageStateText.text = resources.getString(R.string.title_matched)
                binding.imgUserProfile.loadImage(file!!.path)

//                if (requireActivity() is MainActivity) {
//                    isImageUpdate = true
//                    saveData()
//                }

            } else {
                binding.changeImageStateText.setTextColor(resources.getColor(R.color.delete))
                binding.changeImageStateText.text = resources.getString(R.string.title_not_matched)

                val builder = AlertDialog.Builder(requireContext())

                builder.setTitle("Choose a clear photo")
                    .setMessage("Make the best first impression by choosing a photo that clearly shows your face for your first picture.")
                    .setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                        })

                builder.create().show()
            }

            Timer().schedule(2000) {
                requireActivity().runOnUiThread {
                    binding.changeImageStateText.hide()
                }
            }
        }

        viewModel.userDataUpdated.observe(viewLifecycleOwner) {

            when (it) {
                is DataState.NewData -> {
                    //if (!isImageUpdate)
                    binding.btnEditProfileSave.hideButtonLoading(
                        resources.getString(R.string.title_updated),
                        requireActivity()
                    ) {
                        when (requireActivity()) {
                            is MainActivity -> {
                                //showToast(resources.getString(R.string.profile_updated))
                                findNavController().popBackStack()
                            }
                            else -> {
                                startActivity(
                                    Intent(
                                        requireActivity(),
                                        MainActivity::class.java
                                    ).apply {
                                        flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    })
                            }
                        }
                    }
                }

                is DataState.FailData -> {

                    binding.btnEditProfileSave.showButtonLoading(resources.getString(R.string.title_save))


                    showToast(it.message)


                }


                else -> {

                    binding.btnEditProfileSave.showButtonLoading(resources.getString(R.string.title_save))

                }

            }

//            when (requireActivity()) {
//                is MainActivity -> {
//                    //showToast(resources.getString(R.string.profile_updated))
//                    findNavController().popBackStack()
//                }
//                else -> {
//                    startActivity(Intent(requireActivity(), MainActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    })
//                }
//            }
        }


        viewModel.getUserInterests()
        viewModel.getUserIam()
        viewModel.getUserPrefer()

        viewModel.myProfileData.observe(viewLifecycleOwner) {

            viewModel.setUserDate(it.birthdate)

            setProfileData(it)

//            checkDeepLink()

        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it && !isSavingNewData) {
                binding.shimmerContainer.container.show()
                binding.editProfileContainer.hide()
                isSavingNewData = true
                //showLoading()
            } else {

                binding.shimmerContainer.container.hide()
                binding.editProfileContainer.show()
                binding.btnEditProfileSave.showButtonLoading(resources.getString(R.string.title_save))

                //hideLoading()
            }
        }

        viewModel.isLoggedOut.observe(viewLifecycleOwner) {
            if (it) {
                startActivity(
                    Intent(
                        requireContext(),
                        AuthActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    })
                (activity as MainActivity).socialMediaLogin.signOut()

            } else {
                binding.btnEditProfileLogout.showButtonLoading(resources.getString(R.string.title_logout))
                showToast("Error while logging out")
            }
        }


    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((requireActivity().checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (requireActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(READ_EXTERNAL_STORAGE, CAMERA)
                val permissionCoarse = arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA)


                requestPermissions(
                    permissionCoarse,
                    RECORD_REQUEST_CODE
                )

                requestPermissions(
                    permission,
                    RECORD_REQUEST_CODE
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001

                // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002


            } else {
//                imagePicker
//                    .title("My Picker")
//                    .multipleSelection(enable = true, maxCount = 5)
//                    .showCountInToolBar(true)
////            .showFolder(true)
////            .cameraIcon(true)
//                    .doneIcon(true)
//
//                    .open(PickerType.GALLERY)
                AdditionalImagesDialog(this).showDialog(requireContext())

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    AdditionalImagesDialog(this).showDialog(requireContext())

                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    private fun goToFragment(
        argumentIdKey: String,
        argumentIdValue: String,
        fragment: Int
    ) {
        val navController = MainActivity().findNavController(
            R.id.nav_host_fragment_activity_main
        )

        val bundle = Bundle()
        bundle.putString(argumentIdKey, argumentIdValue)
        navController.navigate(fragment, bundle)
    }

    private fun setClicks() {

        binding.btnEditProfileAddAdditionalImages.setOnClickListener {
//            AdditionalImagesDialog(this).showDialog(requireContext())
//            checkPermissionForImage()

            if (UserSessionManagement.userNeedToUpdate() == NeedToUpdateStatus.UPDATE_PROFILE.status){
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainMainContainer, AdditionalImagesFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            else
                findNavController().navigate(
                    EditProfileFragmentDirections.actionEditProfileFragmentToAdditionalImagesFragment(
                        additionalImages.toTypedArray()
                    )
                )

        }
        binding.btnEditProfileLogout.setOnClickListener {
            ConfirmationDialog(
                requireContext(),
                getString(R.string.signOutmessage),
                true
            ).showDialog {
                if (it == 1) {
                    binding.btnEditProfileLogout.showButtonLoading(resources.getString(R.string.title_loggin_out))
                    logOut()
                }
            }
        }

        binding.rbProfileRadioGroup.setOnCheckedChangeListener { radioGroup, isChecked ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.rbProfileOther -> binding.otherGenderEditText.show()
                else -> binding.otherGenderEditText.hide()
            }
        }

        binding.btnEditProfileSave.apply {
            //isImageUpdate = false
            setOnClickListener {

//                showButtonLoading(resources.getString(R.string.title_saving))

                binding.btnEditProfileSave.text = resources.getString(R.string.title_saving)
                saveData()

            }
        }


//        binding.btnEditProfileSave.setOnClickListener {
//            saveData()
//        }

        binding.imgUserProfileContainer.setOnClickListener {
            imageUtil.selectImage(false)
        }

        binding.edtProfileDataContainer.setOnClickListener {

            val date = Calendar.getInstance()


            val piker = DatePickerDialog(
                requireContext(), object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        viewModel.userYear = year
                        viewModel.userMonth = month
                        viewModel.userDay = dayOfMonth

                        binding.txtProfileDate.setText("${dayOfMonth}-${month + 1}-${year}")
                    }

                }, date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            )

            piker.datePicker.maxDate = System.currentTimeMillis()
            piker.show()
        }

        binding.edtProfileTagsContainer.setOnClickListener {
            TagsDialogFragment.instance(
                requireActivity().supportFragmentManager,
                "I enjoy...",
                viewModel.allInterestList,
                selectedInterestsTags,
                1,
                this
            )
        }


        binding.edtProfileIamContainer.setOnClickListener {
            TagsDialogFragment.instance(
                requireActivity().supportFragmentManager,
                "I am...",
                viewModel.allIamList,
                selectedIamTags,
                2,
                this
            )
        }

        binding.edtProfilePrefereToContainer.setOnClickListener {
            TagsDialogFragment.instance(
                requireActivity().supportFragmentManager,
                "I prefer to...",
                viewModel.allPreferList,
                selectedPrefersTags,
                3,
                this
            )
        }


    }

    private fun logOut() {
        viewModel.logOut()
    }

    private fun setProfileData(data: UserProfileData) {

        additionalImages = data.userImages ?: emptyList()

        binding.edtUserName.setText(data.userName)
        binding.edtUniversityCode.setText(data.universityCode)
        binding.txtProfileDate.setText(data.birthdate)
        binding.rbProfileRadioGroup.checkedRadioButtonId

        if (!data.userImage.isNullOrEmpty())
            binding.imgUserProfile.loadImage(data.userImage)
        when (data.isMale) {
            GenderEnum.MALE -> {
                binding.rbProfileRadioGroup.check(R.id.rbProfileMale)
            }
            GenderEnum.FEMALE -> {
                binding.rbProfileRadioGroup.check(R.id.rbProfileFemale)
            }
            GenderEnum.OTHER -> {
                binding.rbProfileRadioGroup.check(R.id.rbProfileOther)
                binding.otherGenderEditText.setText(data.otherGenderName)
                binding.otherGenderEditText.show()
            }

            else -> {}
        }
        binding.edProfileAboutMe.setText(data.bio)

        //Check if NeedToUpdate User can Logout from EditProfile.
        if (data.needUpdate == NeedToUpdateStatus.UPDATE_PROFILE.status) {
            binding.btnEditProfileLogout.visibility = View.VISIBLE
            binding.imgEditProfileBack.hide()
        }

        setTags(1, data.listoftagsmodel)

        setTags(2, data.iamList)
        setTags(3, data.prefertoList)


    }


    private fun setTags(tagsType: Int, data: List<TagsModel>) {

        Log.i(TAG, "setTags: $data")

        when (tagsType) {
            ProfileTagsEnum.IS_PROFILE_TAG_INTEREST.type -> {
                if (!data.isNullOrEmpty()) binding.txtProfileTags.visibility = View.GONE
                selectedInterestsTags.clear()
                selectedInterestsTags.addAll(data)
                binding.ChipTags.removeAllViews()

                setSelectedTags(binding.ChipTags, data, tagsType)

                checkDeepLink(data)
            }

            ProfileTagsEnum.IS_PROFILE_TAG_IAM.type -> {
                if (!data.isNullOrEmpty()) binding.txtProfileIam.visibility = View.GONE

                selectedIamTags.clear()
                selectedIamTags.addAll(data)
                binding.ChipIam.removeAllViews()
                setSelectedTags(binding.ChipIam, data, tagsType)
            }
            ProfileTagsEnum.IS_PROFILE_TAG_PREFER.type -> {
                if (!data.isNullOrEmpty()) binding.txtProfilePrefereTo.visibility = View.GONE
                selectedPrefersTags.clear()
                selectedPrefersTags.addAll(data)
                binding.ChipPrefereTo.removeAllViews()
                setSelectedTags(binding.ChipPrefereTo, data, tagsType)
            }
        }

    }

    private fun setSelectedTags(chipView: ChipGroup, data: List<TagsModel>, tagType: Int) {


        val selectedTag = ArrayList(data)

        Log.i(TAG, "setSelectedTags: $selectedTag")

        ProfileUtil.addChipsViews(requireContext(), chipView, selectedTag, true)

        when (tagType) {
            ProfileTagsEnum.IS_PROFILE_TAG_INTEREST.type -> {
                selectedInterestsTags.clear()
                selectedInterestsTags.addAll(selectedTag)

            }
            ProfileTagsEnum.IS_PROFILE_TAG_IAM.type -> {
                selectedIamTags.clear()




                if (selectedTag.size == 0) {
                    val iamHashTag = UserSessionManagement.getIamHashTag()

                    selectedTag.add(iamHashTag)
                } else {
                    // set condition here to delete #
                    for (s in selectedTag) {
                        if (!s.tagname.equals("#")) {
                            selectedIamTags.add(s)

                        }

                    }

//                    selectedIamTags.addAll(selectedTag)

                }
            }
            ProfileTagsEnum.IS_PROFILE_TAG_PREFER.type -> {

                selectedPrefersTags.clear()



                if (selectedTag.size == 0) {

                    val iPrefereHashTag = UserSessionManagement.getIPrefereHashTag()

                    selectedTag.add(iPrefereHashTag)
                } else {
                    // set condition here to delete #
                    for (s in selectedTag) {
                        if (!s.tagname.equals("#")) {
                            selectedPrefersTags.add(s)

                        }

                    }

//                    selectedPrefersTags.addAll(selectedTag)

                }

            }
        }


    }

    private fun saveData() {
        binding.edProfileAboutMeContainer.error = ""

        val dat = Calendar.getInstance()
        dat.set(Calendar.YEAR, viewModel.userYear)
        dat.set(Calendar.MONTH, viewModel.userMonth)
        dat.set(Calendar.DAY_OF_MONTH, viewModel.userDay)

        //val strDate = SimpleDateFormat("dd-MM-yyyy").format(dat)
        val strDate = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val formatDate = strDate.format(dat.time)

        val userName = binding.edtUserName.text.toString()
        val universityCode = binding.edtUniversityCode.text.toString()

        when {


            selectedInterestsTags.isNullOrEmpty() -> {
                binding.btnEditProfileSave.text = resources.getString(R.string.title_save)
                showToast(getString(R.string.select_tag_enjoy_message))
                return
            }


//            selectedIamTags.isNullOrEmpty() -> {
//                val iamHashTag = UserSessionManagement.getIamHashTag()
//                selectedIamTags.add(iamHashTag)
////                binding.btnEditProfileSave.text = resources.getString(R.string.title_save)
////                showToast(getString(R.string.select_tag_iam_message))
//
//
//                return
//            }
//            selectedPrefersTags.isNullOrEmpty() -> {
////                binding.btnEditProfileSave.text = resources.getString(R.string.title_save)
////                showToast(getString(R.string.select_tag_prefer_message))
//
//
//                val iPrefereHashTag = UserSessionManagement.getIPrefereHashTag()
//                selectedPrefersTags.add(iPrefereHashTag)
//
//                return
//            }


            else -> {

                var genderOther: String? = null
                var gender: String?


                when (binding.rbProfileRadioGroup.checkedRadioButtonId) {


                    R.id.rbProfileMale -> {
                        gender = "male"
                    }
                    R.id.rbProfileFemale -> {
                        gender = "female"
                    }
                    else -> {
                        gender = "other"
                        genderOther = binding.otherGenderEditText.text.toString().trim()
                    }

                }

                var bio = binding.edProfileAboutMe.text.toString()

//                if (bio.isEmpty()) {
////                    binding.btnEditProfileSave.showButtonLoading(resources.getString(R.string.title_save))
////                    binding.edProfileAboutMeContainer.error =
////                        resources.getString(R.string.enter_bio)
//                    bio = "."
//                    return
//                }


                val strIdsInterest = convertTagsToUpdate(selectedInterestsTags)


                if (selectedIamTags.size == 0) {
                    val iamHashTag = UserSessionManagement.getIamHashTag()
                    selectedIamTags.add(iamHashTag)
                }
                val strIdsIam = convertTagsToUpdate(selectedIamTags)

                if (selectedPrefersTags.size == 0) {

                    val iPrefereHashTag = UserSessionManagement.getIPrefereHashTag()
                    selectedPrefersTags.add(iPrefereHashTag)
                }

                val strIdsPrefer = convertTagsToUpdate(selectedPrefersTags)

                if (file == null && viewModel.myProfileData.value!!.userImage.isNullOrEmpty()) {
                    binding.btnEditProfileSave.showButtonLoading(resources.getString(R.string.title_save))
                    showToast(resources.getString(R.string.select_user_image))
                    return
                } else {

                    if (selectedIamTags.isNullOrEmpty()) {
                        val iamHashTag = UserSessionManagement.getIamHashTag()
                        selectedIamTags.add(iamHashTag)
                    }

                    if (selectedPrefersTags.isNullOrEmpty()) {
                        val iPrefereHashTag = UserSessionManagement.getIPrefereHashTag()
                        selectedPrefersTags.add(iPrefereHashTag)
                    }

                    if (bio.isEmpty()) {
                        bio = "."
                    }

                    if (binding.rbProfileRadioGroup.checkedRadioButtonId == -1) {
                        binding.btnEditProfileSave.text = resources.getString(R.string.title_save)
                        showToast(resources.getString(R.string.select_user_gender))

                    } else {

                        viewModel.updateData(
                            userName,
                            universityCode,
                            formatDate,
                            gender,
                            genderOther,
                            bio,
                            strIdsInterest,
                            strIdsIam,
                            strIdsPrefer,
                            file
                        )

                    }


                }


            }
        }


    }

    private fun convertTagsToUpdate(data: List<TagsModel>): String {
        var strIDS = "["
        data.forEach {
            strIDS += "\"${it.tagID}\","
        }

        strIDS = strIDS.substring(0, strIDS.length - 1)
        strIDS += "]"

        return strIDS
    }

    override fun onSave(tagType: Int, selectedTags: ArrayList<TagsModel>) {
        setTags(tagType, selectedTags)
    }

    override fun onImagePick(uri: Uri?) {
        showToast("hi single")


    }

    override fun onMultiImagePick(uris: List<Uri>?) {
        showToast("hi multi")
        AdditionalImagesDialog(this).hideDialog()
        AdditionalImagesDialog(this).showDialog(requireContext(), uris)


    }

    override fun onAddImages() {
//        imagePicker
//            .title("Add additional images")
//            .multipleSelection(enable = true, maxCount = 5)
//            .showCountInToolBar(true)
//            .showFolder(true)
//            .cameraIcon(true)
//            .doneIcon(true)
//            .allowCropping(true)
//            .compressImage(false)
//            .extension(PickExtension.ALL)
//        imagePicker.open(PickerType.GALLERY)


    }


}


