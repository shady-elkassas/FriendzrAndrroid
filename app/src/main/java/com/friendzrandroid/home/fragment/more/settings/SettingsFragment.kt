package com.friendzrandroid.home.fragment.more.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.UserSessionManagement.saveUserSettings
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.databinding.SettingsFragmentBinding
import com.friendzrandroid.home.data.model.ConfigurationValidation
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.UserSettingsResponse
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.dialog.filterDialog.FilterDialogFragment
import com.friendzrandroid.home.dialog.filterDialog.FilterDialogListener
import com.friendzrandroid.home.dialog.gostModeDialog.AllowMyApperanceDialog
import com.friendzrandroid.home.fragment.home.feed.FeedViewModel
import com.friendzrandroid.utils.AdsBannerUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SettingsFragment : BaseFragment(), FilterDialogListener {

    private val binding: SettingsFragmentBinding by lazy(LazyThreadSafetyMode.NONE) {
        SettingsFragmentBinding.inflate(layoutInflater)
    }

    val args: SettingsFragmentArgs by navArgs()


    val personalSpace by lazy {
        Log.e("personalSpace", args.personalSpace)
        args.personalSpace
    }
    val privateMode by lazy {
        Log.e("eventDetails", args.privateMode)
        args.privateMode
    }
    val ageFilter by lazy {
        Log.e("eventDetails", args.ageFilter)
        args.ageFilter
    }
    val distanceFilter by lazy {
        Log.e("eventDetails", args.distanceFilter)
        args.distanceFilter
    }


    private val feedViewModel: FeedViewModel by viewModels()

    private val viewModel: SettingsViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel


    private lateinit var userSettingsData: UserSettingsResponse

    private val TAG = "SettingsFragment"

    private var showDialog: Boolean = true
    private var isNotificationTouched: Boolean = true
    private var isGhostModeTouched: Boolean = true
    private var isPersonalSpaceTouched: Boolean = true
    private var isManualDistanceControlTouched: Boolean = true
    private var isFilterAgeTouched: Boolean = true
    private var showAgeDialog: Boolean = true
    private var showDistanceDialog: Boolean = true
    private var deepLink: Boolean = true

    private var showNotificationDialog: Boolean = true
    private var showPersonalSpaceDialog: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AdsBannerUtil.loadAds(binding.bannerAdView)

        binding.userImage.loadImage(UserSessionManagement.userImage())

        viewModel.getSettings()
        settingResponseStateObserver()

        settingsUpdatedObserver()
        viewModel.getConfigurationValidation()

        viewModel.configurationResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NewData -> setConfiguration(it.data)
                is DataState.FailData -> showToast(it.message)
                else -> {}
            }


        }
        //setClicks()


        return binding.root
    }

    private fun settingResponseStateObserver() {
        viewModel.settingResponseState.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsViewModel.GetSettingsDataState.Success -> {
                    setSwitches(it.data)
                    Log.i(TAG, "onCreateView: ${it.data}")
                    appendPrivateModeString(it.data.myAppearanceTypes)


                }

                is SettingsViewModel.GetSettingsDataState.Fail -> {
                    showToast(it.message)
                }
            }
        }


    }

    private fun settingsUpdatedObserver() {


        viewModel.settingsUpdated.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsViewModel.UpdateSettingsDataState.Success -> {
                    hideLoading()
                    setSwitches(it.data)
                    val myAppearanceTypes = it.data.myAppearanceTypes
                    appendPrivateModeString(myAppearanceTypes)
                    //findNavController().popBackStack()
                }

                is SettingsViewModel.UpdateSettingsDataState.Fail -> {
                    hideLoading()
                    showToast(it.message)
                }
            }
        }

    }

    private fun setConfiguration(data: ConfigurationValidation) {
        UserSessionManagement.saveConfigurationValidation(data)
    }

    private fun setSwitches(data: UserSettingsResponse) {

        userSettingsData = data

        binding.switchPushNotification.isChecked = data.pushnotification


//        binding.switchGostMode.isChecked = data.ghostmode
        binding.switchAllowMyLocation.isChecked = data.allowmylocation
//        binding.switchFilterAge.isChecked = data.filteringaccordingtoage


//        binding.switchManualDistanceControl.isChecked = data.distanceFilter
        handlePrivateModeIcon()




        handlePersonalSpaceConditionWithDeepLinkToShowOrHideConfirmationDialog(data)
        handlePrivateModeConditionWithDeepLinkToShowOrHideConfirmationDialog(data)
        handleAgeFilterConditionWithDeepLinkToShowOrHideConfirmationDialog(data)
        handleDistanceFilterConditionWithDeepLinkToShowOrHideConfirmationDialog(data)


















        setClicks()
        setSwitchesClicks(userSettingsData)


    }

    private fun handleDistanceFilterConditionWithDeepLinkToShowOrHideConfirmationDialog(data: UserSettingsResponse) {

        if (distanceFilter != null && !distanceFilter.equals("")) {

            if (deepLink) {
                deepLink = false
                isManualDistanceControlTouched = false

                binding.switchManualDistanceControl.isChecked = true

                showBottomFragmentFilterSlider(
                    dialogTitle = getString(R.string.distance_filter),
                    distanceFilter = data.manualdistancecontrol,
                    listener = this
                )

            }

            /*  if (!data.distanceFilter) {
                  if (deepLink) {
                      deepLink = false
                      isManualDistanceControlTouched = false

                      binding.switchManualDistanceControl.isChecked = true

                      showBottomFragmentFilterSlider(
                          dialogTitle = getString(R.string.distance_filter),
                          distanceFilter = data.manualdistancecontrol,
                          listener = this
                      )

                  }
              } else {
                  binding.switchManualDistanceControl.isChecked = data.distanceFilter

              }*/


        } else {

            binding.switchManualDistanceControl.isChecked = data.distanceFilter
        }
    }

    private fun handleAgeFilterConditionWithDeepLinkToShowOrHideConfirmationDialog(data: UserSettingsResponse) {

        if (ageFilter != null && !ageFilter.equals("")) {
            if (deepLink) {
                deepLink = false
                isFilterAgeTouched = false

                binding.switchFilterAge.isChecked = true

                showBottomFragmentFilterSlider(
                    dialogTitle = getString(R.string.age_filter),
                    ageFrom = data.agefrom,
                    ageTo = data.ageto,
                    listener = this
                )
            }
            /*     if (!data.filteringaccordingtoage) {
                     if (deepLink) {
                         deepLink = false
                         isFilterAgeTouched = false

                         binding.switchFilterAge.isChecked = true

                         showBottomFragmentFilterSlider(
                             dialogTitle = getString(R.string.age_filter),
                             ageFrom = data.agefrom,
                             ageTo = data.ageto,
                             listener = this
                         )

                     }
                 } else {
                     binding.switchFilterAge.isChecked = data.filteringaccordingtoage

                 }*/


        } else {

            binding.switchFilterAge.isChecked = data.filteringaccordingtoage
        }
    }

    private fun handlePrivateModeConditionWithDeepLinkToShowOrHideConfirmationDialog(data: UserSettingsResponse) {
        if (privateMode != null && !privateMode.equals("")) {
            if (deepLink) {
                deepLink = false
                isGhostModeTouched = false

                binding.switchGostMode.isChecked = true
                handlePrivateModeIcon()
                showPrivateModeDialog()

            }


            /* if (!data.ghostmode) {
                 if (deepLink) {
                     deepLink = false
                     isGhostModeTouched = false

                     binding.switchGostMode.isChecked = true
                     handlePrivateModeIcon()
                     showPrivateModeDialog()

                 }
             } else {
                 binding.switchGostMode.isChecked = data.ghostmode

             }*/


        } else {

            binding.switchGostMode.isChecked = data.ghostmode
        }
    }

    private fun handlePersonalSpaceConditionWithDeepLinkToShowOrHideConfirmationDialog(data: UserSettingsResponse) {

        if (personalSpace != null && !personalSpace.equals("")) {

            if (!data.personalSpace) {
                if (deepLink) {
                    deepLink = false
                    isPersonalSpaceTouched = false

                    binding.switchPersonalSpace.isChecked = true

                    showConfirmationDialog(
                        getString(R.string.dialog_personal_space_on_message),
                        binding.switchPersonalSpace
                    )
                }
            } else {
                binding.switchPersonalSpace.isChecked = data.personalSpace

            }


        } else {

            binding.switchPersonalSpace.isChecked = data.personalSpace
        }
    }


    private fun deleteAccount() {

        ConfirmationDialog(
            requireContext(),
            getString(R.string.delete_account_message),
            true
        ).showDialog {
            if (it == 0) {
                binding.switchGostMode.isChecked = false
            } else {
                viewModel.deleteAccount()

            }
        }
    }

    private fun setClicks() {

        binding.settingsDeleteAccount.setOnClickListener {
            deleteAccount()
        }

        binding.settingBlockList.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_blockListFragment)
        }

        binding.settingsChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
        }
    }

    private fun setSwitchesClicks(data: UserSettingsResponse) {
        binding.switchPushNotification.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, p1: MotionEvent): Boolean {
                isNotificationTouched = true;
                return false
            }
        })

        binding.switchPushNotification.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isNotificationTouched) {
                isNotificationTouched = false

                if (!isChecked && showNotificationDialog)
                    showConfirmationDialog(
                        getString(R.string.dialog_notification_message),
                        binding.switchPushNotification
                    )
                else if (isChecked && showNotificationDialog)
                    showConfirmationDialog(
                        getString(R.string.dialog_notification_on_message),
                        binding.switchPushNotification
                    )


            }
//            saveUserSettings()
        }





        binding.switchGostMode.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, p1: MotionEvent): Boolean {
                isGhostModeTouched = true;
                return false
            }
        })

        binding.switchGostMode.setOnCheckedChangeListener { compoundButton, isChecked ->

            handlePrivateModeIcon()
            if (isGhostModeTouched) {
                isGhostModeTouched = false


                if (!isChecked && showDialog) {
                    showConfirmationDialog(
                        getString(R.string.dialog_private_mode_message),
                        binding.switchGostMode
                    )
                    UserSessionManagement.saveSettingsPrivateModeStatus(true)

                } else if (isChecked && showDialog) {

                    showPrivateModeDialog()
                }

            }

        }










        binding.switchPersonalSpace.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, p1: MotionEvent): Boolean {
                isPersonalSpaceTouched = true;
                return false
            }
        })

        binding.switchPersonalSpace.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isPersonalSpaceTouched) {

                isPersonalSpaceTouched = false

                if (!isChecked && showPersonalSpaceDialog)
                    showConfirmationDialog(
                        getString(R.string.dialog_personal_space_message),
                        binding.switchPersonalSpace
                    )
                else if (isChecked && showPersonalSpaceDialog)

                    showConfirmationDialog(
                        getString(R.string.dialog_personal_space_on_message),
                        binding.switchPersonalSpace
                    )
            }
            saveUserSettings()
        }




        binding.switchManualDistanceControl.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, p1: MotionEvent): Boolean {
                isManualDistanceControlTouched = true;
                return false
            }
        })
        binding.llSettingsManualDistanceControlContainer.setOnClickListener {
            if (binding.switchManualDistanceControl.isChecked)
                showBottomFragmentFilterSlider(
                    dialogTitle = getString(R.string.distance_filter),
                    distanceFilter = data.manualdistancecontrol,
                    listener = this
                )

        }

        binding.switchManualDistanceControl.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isManualDistanceControlTouched) {
                isManualDistanceControlTouched = false


                if (isChecked && showDistanceDialog)
                    showBottomFragmentFilterSlider(
                        dialogTitle = getString(R.string.distance_filter),
                        distanceFilter = data.manualdistancecontrol,
                        listener = this
                    )
                else if (!isChecked && showDistanceDialog)
                    showConfirmationDialog(
                        getString(R.string.dialog_distance_filter_message),
                        binding.switchManualDistanceControl
                    )

            }
        }




        binding.switchFilterAge.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, p1: MotionEvent): Boolean {
                isFilterAgeTouched = true;
                return false
            }
        })

        binding.switchFilterAge.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isFilterAgeTouched) {
                isFilterAgeTouched = false


                if (isChecked && showAgeDialog)
                    showBottomFragmentFilterSlider(
                        dialogTitle = getString(R.string.age_filter),
                        ageFrom = data.agefrom,
                        ageTo = data.ageto,
                        listener = this
                    )
                else if (!isChecked && showAgeDialog)
                    showConfirmationDialog(
                        getString(R.string.dialog_age_filter_message),
                        binding.switchFilterAge
                    )

            }

        }

        binding.llSettingsFilterAccordingAgeContainer.setOnClickListener {
            if (binding.switchFilterAge.isChecked) {
                showBottomFragmentFilterSlider(
                    dialogTitle = getString(R.string.age_filter),
                    ageFrom = data.agefrom,
                    ageTo = data.ageto,
                    listener = this
                )
            }

        }
    }

    private fun handlePrivateModeIcon() {
        if (binding.switchGostMode.isChecked)
            binding.imgSettingsGhostModeIcon.setImageResource(R.drawable.ic_private_mode_on)
        else
            binding.imgSettingsGhostModeIcon.setImageResource(R.drawable.ic_private_mode_off)

    }

    private fun showConfirmationDialog(message: String, compoundButton: CompoundButton) {
        ConfirmationDialog(
            requireContext(),
            message,
            true
        ).showDialog {

            when (compoundButton.tag) {
                "notification_button" ->
                    if (it == 1 && compoundButton.isChecked) {
                        showNotificationDialog = false
                        compoundButton.isChecked = true
                        showNotificationDialog = true
                    } else if (it == 1 && !compoundButton.isChecked) {
                        showNotificationDialog = false
                        compoundButton.isChecked = false
                        showNotificationDialog = true
                    } else {
                        showNotificationDialog = false
                        compoundButton.isChecked = !compoundButton.isChecked
                        showNotificationDialog = true
                    }

                "personal_space_button" ->
                    if (it == 1 && compoundButton.isChecked) {
                        showPersonalSpaceDialog = false
                        compoundButton.isChecked = true
                        showPersonalSpaceDialog = true
                    } else if (it == 1 && !compoundButton.isChecked) {

                        showPersonalSpaceDialog = false
                        compoundButton.isChecked = false
                        showPersonalSpaceDialog = true

/*                        if (personalSpace != null && !personalSpace.equals("")) {

                            // handle here its closed dialog or not
                            if (message.equals("Are you sure you want to turn off personal space?")) {

                                showPersonalSpaceDialog = false
                                compoundButton.isChecked = false
                                showPersonalSpaceDialog = true

                            } else {

                                showPersonalSpaceDialog = false
                                compoundButton.isChecked = true
                                showPersonalSpaceDialog = true
                            }




                        } else {

                            showPersonalSpaceDialog = false
                            compoundButton.isChecked = false
                            showPersonalSpaceDialog = true
                        }*/


                    } else {
                        showPersonalSpaceDialog = false
                        compoundButton.isChecked = !compoundButton.isChecked
                        showPersonalSpaceDialog = true
                    }

                else -> if (it == 1) {
                    compoundButton.isChecked = false
                    //saveUserSettings()
                    when (compoundButton.tag) {
                        "DistanceFilterTag" -> showDistanceDialog = true
                        "AgeFilterTag" -> showAgeDialog = true
                        "PrivateModeTag" -> showDialog = true
                    }
                } else {

                    when (compoundButton.tag) {
                        "DistanceFilterTag" -> {
                            showDistanceDialog = false
                            compoundButton.isChecked = !compoundButton.isChecked
                            showDistanceDialog = true
                        }
                        "AgeFilterTag" -> {
                            showAgeDialog = false
                            compoundButton.isChecked = !compoundButton.isChecked
                            showAgeDialog = true
                        }
                        "PrivateModeTag" -> {
                            showDialog = false
                            compoundButton.isChecked = !compoundButton.isChecked
                            showDialog = true
                        }
                    }
                }
            }

            saveUserSettings()

        }
    }


    private fun showDeepLinkConfirmationDialog(
        message: String,
        compoundButton: CompoundButton,
        isCheck: Boolean
    ) {
        ConfirmationDialog(
            requireContext(),
            message,
            true
        ).showDialog {

            when (compoundButton.tag) {

                "personal_space_button" ->
                    if (it == 1 && isCheck) {
                        showPersonalSpaceDialog = false
                        compoundButton.isChecked = true
                        showPersonalSpaceDialog = true

                    } else if (it == 1 && !isCheck) {

                        showPersonalSpaceDialog = false
                        compoundButton.isChecked = false
                        showPersonalSpaceDialog = true

                    } else {
//                        if (isCheck) {
//
//                            showPersonalSpaceDialog = false
//                            compoundButton.isChecked = false
//                            showPersonalSpaceDialog = false
//                        } else {
//
//                        }
                    }

                else -> if (it == 1) {
                    compoundButton.isChecked = false
                    //saveUserSettings()
                    when (compoundButton.tag) {
                        "DistanceFilterTag" -> showDistanceDialog = true
                        "AgeFilterTag" -> showAgeDialog = true
                        "PrivateModeTag" -> showDialog = true
                    }
                } else {

                    when (compoundButton.tag) {
                        "DistanceFilterTag" -> {
                            showDistanceDialog = false
                            compoundButton.isChecked = !compoundButton.isChecked
                            showDistanceDialog = true
                        }
                        "AgeFilterTag" -> {
                            showAgeDialog = false
                            compoundButton.isChecked = !compoundButton.isChecked
                            showAgeDialog = true
                        }
                        "PrivateModeTag" -> {
                            showDialog = false
                            compoundButton.isChecked = !compoundButton.isChecked
                            showDialog = true
                        }
                    }
                }
            }

            saveUserSettings()

        }
    }

    private fun showPrivateModeDialog() {

        AllowMyApperanceDialog(requireContext()).showDialog {
            if (it.size == 0) {
                showDialog = false
                binding.switchGostMode.isChecked = false
                showDialog = true
                binding.tvSettingsGhostModeItemValue.text = ""
//                feedViewModel.privateModeState.value = false
                UserSessionManagement.saveSettingsPrivateModeStatus(true)


            } else {
                userSettingsData = userSettingsData.copy(myAppearanceTypes = it)
                Log.i(TAG, "showPrivateModeDialog: $userSettingsData")
                saveUserSettings()


//                feedViewModel.privateModeState.value = true

                appendPrivateModeString(it)

                UserSessionManagement.saveSettingsPrivateModeStatus(true)


            }
        }

    }

    private fun appendPrivateModeString(it: ArrayList<Int>) {
        val stringBuilder = StringBuilder()
        var interestsList: ArrayList<String> = ArrayList()

        if (it.size > 0) {
            UserSessionManagement.saveSettingsPrivateModeStatus(true)

            it.forEach {

                if (it == 0) {
                    stringBuilder.append("")
                } else if (it == 1) {
                    interestsList.add("Everyone")

                } else if (it == 2) {
                    interestsList.add("Men")

                } else if (it == 3) {
                    interestsList.add("Women")

                } else if (it == 4) {
                    interestsList.add("Other gender")

                } else {

                    interestsList = ArrayList()


                }
            }
//            feedViewModel.privateModeState.value = true


        } else {
//            feedViewModel.privateModeState.value = false
            binding.tvSettingsGhostModeItemValue.text = "strIDS"

        }

        val toList = stringBuilder.toList()
        val separator = ", "

        if (interestsList.size > 0) {
            var strIDS = ""
            interestsList.forEach {
                strIDS += it + ","
            }

            strIDS = strIDS.substring(0, strIDS.length - 1)

            binding.tvSettingsGhostModeItemValue.text = strIDS
        } else {
            binding.tvSettingsGhostModeItemValue.text = ""

        }

    }

    private fun showBottomFragmentFilterSlider(
        dialogTitle: String,
        ageFrom: Int? = null,
        ageTo: Int? = null,
        distanceFilter: Double? = null,
        listener: FilterDialogListener
    ) {

        FilterDialogFragment.instance(
            requireActivity().supportFragmentManager,
            dialogTitle,
            ageFrom,
            ageTo,
            distanceFilter,
            listener
        )
    }


    private fun saveUserSettings() {

        Log.i(TAG, "saveUserSettings: $userSettingsData")
        Log.i(TAG, "saveUserSettings: Ghost: ${binding.switchGostMode.isChecked}")

        viewModel.updateSettings(
            pushnotification = binding.switchPushNotification.isChecked,
            allowmylocation = binding.switchAllowMyLocation.isChecked,
            ghostmode = binding.switchGostMode.isChecked,
            manualdistancecontrol = userSettingsData.manualdistancecontrol,
            filteringaccordingtoage = binding.switchFilterAge.isChecked,
            agefrom = userSettingsData.agefrom,
            ageto = userSettingsData.ageto,
            myAppearanceTypes = userSettingsData.myAppearanceTypes.toString(),
            personalSpace = binding.switchPersonalSpace.isChecked,
            distanceFilter = binding.switchManualDistanceControl.isChecked,
        )
    }


    // CallBack from Slider Dialog..
    override fun onSave(ageFrom: Int, ageTo: Int, distance: Double, isAgeSlider: Boolean) {

        //Update the user setting data to be uploaded to the (Server)
        userSettingsData = if (isAgeSlider)
            userSettingsData.copy(
                agefrom = ageFrom,
                ageto = ageTo,
            )
        else
            userSettingsData.copy(
                manualdistancecontrol = distance
            )

        Log.e(TAG, "onSave: $userSettingsData")

        saveUserSettings()
    }

    override fun onDismissed(isAgeFilter: Boolean) {

        if (isAgeFilter) {

            binding.switchFilterAge.isChecked = false
            showAgeDialog = true
        } else {
            binding.switchManualDistanceControl.isChecked = false
            showDistanceDialog = true
        }
    }

    override fun onResume() {
        super.onResume()
        ConfirmationDialog(
            requireContext(),
            "", true
        ).hideDialog()


    }

}