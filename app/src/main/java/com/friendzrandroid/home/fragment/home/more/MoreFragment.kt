package com.friendzrandroid.home.fragment.home.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.MoreFragmentBinding
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.friendzrandroid.home.fragment.more.settings.SettingsViewModel
import com.friendzrandroid.splash.presentation.activity.IntroActivity
import com.friendzrandroid.utils.AdsBannerUtil
import com.friendzrandroid.utils.HelperClass.showWebViewDialogScreen
import com.friendzrandroid.utils.ImageDialog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlin.math.log


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MoreFragment : BaseFragment() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        MoreFragmentBinding.inflate(layoutInflater)
    }
    private val viewModel: SettingsViewModel by viewModels()

    private val moreViewModel: MoreViewModel by viewModels()


    override val baseViewModel: BaseViewModel
        get() = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AdsBannerUtil.loadAds(binding.bannerAdView)
        goToProfile()
        goToEvents()
        goToNotifications()
        goToSettings()
        goToContactUs()
        goToAboutUs()
        goToTermsAndConditions()
        goToPrivacyPolicy()
        goToHelp()
        goToTipsAndGuidance()
        share()
        logout()

    }

    private fun goToNotifications() {
        UserSessionManagement.updateNotificationNumber(0)
        binding.tvMoreNotification.setOnClickListener {
            val action = MoreFragmentDirections.actionNavigationMoreToNotificationFragment()
            findNavController().navigate(action)
        }

    }


    private fun goToProfile() {

        binding.tvMoreProfile.setOnClickListener {
            val action = MoreFragmentDirections.actionNavigationMoreToProfileFragment()
            findNavController().navigate(action)

        }

    }

    private fun goToEvents() {
        binding.tvMoreEvents.setOnClickListener {
            val action = MoreFragmentDirections.actionNavigationMoreToEventFragment()
            findNavController().navigate(action)

        }
    }

    override fun onResume() {
        super.onResume()
        binding.tvMoreUserName.text = UserSessionManagement.userName()
        binding.imgMoreUserImage.loadImage(UserSessionManagement.userImage())
        binding.imgMoreUserImage.setOnClickListener {
            ImageDialog.setImageBigger(requireActivity(), UserSessionManagement.userImage())
        }

        val notificationCount = UserSessionManagement.getNotificationNumber()
        Log.e("TAG", "onResume: $notificationCount")
        binding.notificationBadge.apply {
            text = notificationCount.toString()
            if (notificationCount > 0) binding.notificationBadgeContainer.show()
            else binding.notificationBadgeContainer.hide()
        }

    }


    private fun logout() {

        binding.tvMoreLogout.setOnClickListener {

//            val builder: AlertDialog.Builder? = activity?.let {
//                AlertDialog.Builder(it)
//            }
//
//            builder?.setTitle(getString(R.string.caution))
//
//            builder?.setMessage(getString(R.string.signOutmessage))
//
//            builder?.setIcon(R.drawable.ic_caution_sign)
//
//            builder?.setPositiveButton(
//                getString(R.string.yes)
//            ) { dialog, which -> setLogOutCall() }
//
//            builder?.setNegativeButton(
//                getString(R.string.no)
//            ) { dialog, which -> dialog.cancel() }
//
//            builder?.show()

            ConfirmationDialog(
                requireContext(),
                getString(R.string.signOutmessage),
                true
            ).showDialog {
                if (it == 1) {

                    setLogOutCall()

                }
            }
        }


    }

    private fun setLogOutCall() {
        lifecycleScope.launchWhenCreated {
            showLoading()
            val resposne = viewModel.validateResponse(viewModel.logoutUseCase.execute())
            resposne?.let {
                if (it.isSuccessful) {

                    FirebaseMessaging.getInstance().deleteToken()
                    hideLoading()
                    UserSessionManagement.removeUserSession()

                    delay(100)
                    startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    })


                    (activity as MainActivity).socialMediaLogin.signOut()

                }
            }
        }


    }

    private fun share() {

        binding.tvMoreShare.setOnClickListener {
            countClickCall("Share")
            var link: String = ""

            moreViewModel.moreSuccessData.observe(viewLifecycleOwner) {
                val link = it


            }

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, activity?.getString(R.string.url_share))
                type = "text/plain"
            }

//                putExtra(Intent.EXTRA_TEXT, activity?.getString(R.string.url_share))


            activity?.let {
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

        }


    }

    private fun goToTermsAndConditions() {

        binding.tvMoreTerms.setOnClickListener {
            var link: String = ""


            countClickCall("TermsAndConditions")
            moreViewModel.moreSuccessData.observe(viewLifecycleOwner) {
                val link = it
                Log.e("testCount", "click screen is  share  and countClickCall is:  $it")


            }

            activity?.let {
                showWebViewDialogScreen(
                    it,
                    it.getString(R.string.url_terms),
                    it.getString(R.string.title_termsAndCondotions)
                )
            }

        }

    }

    private fun goToPrivacyPolicy() {

        binding.tvPrivacyPolicy.setOnClickListener {
            var link: String = ""

            countClickCall("PrivacyPolicy")
            moreViewModel.moreSuccessData.observe(viewLifecycleOwner) {
                val link = it
                Log.e("testCount", "click screen is  share  and countClickCall is:  $it")


//                it.getString(R.string.url_privacy)
            }


            activity?.let {
                showWebViewDialogScreen(
                    it,
                    it.getString(R.string.url_privacy),
                    it.getString(R.string.title_privacy_policy)
                )
            }
        }

    }

    private fun goToAboutUs() {

        binding.tvMoreAboutUs.setOnClickListener {
            var link: String = ""

            countClickCall("AboutUs")
            moreViewModel.moreSuccessData.observe(viewLifecycleOwner) {


                val link = it
                Log.e("testCount", "click screen is  share  and countClickCall is:  $it")


            }

            activity?.let {
                showWebViewDialogScreen(
                    it,
                    it.getString(R.string.url_aboutUs),
                    it.getString(R.string.title_aboutUs)
                )
            }

        }


    }


    private fun goToContactUs() {

        binding.tvMoreContactUs.setOnClickListener {
            var link: String = ""
            countClickCall("SupportRequest")
            moreViewModel.moreSuccessData.observe(viewLifecycleOwner) {
                link = it
                Log.e("testCount", "click screen is  share  and countClickCall is:  $it")


            }
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            sendIntent.data = Uri.parse("mailto:") // only email apps should handle this

//                sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@friendzr.com"))
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@friendzr.com"))
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestions")
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        }

    }


    private fun goToHelp() {

        val bundle = Bundle()
        bundle.putBoolean("isFromHelp", true)

        binding.tvMoreHelp.setOnClickListener {
            countClickCall("Help")
            moreViewModel.moreSuccessData.observe(viewLifecycleOwner) {

                Log.e("testCount", "click screen is  share  and countClickCall is:  $it")

            }

            startActivity(
                Intent(requireContext(), IntroActivity::class.java).putExtras(bundle)
            )
        }
    }

    private fun goToTipsAndGuidance() {


        binding.tvMoreTips.setOnClickListener {

            var link: String = ""
            countClickCall("TipsAndGuidance")
            moreViewModel.moreSuccessData.observe(viewLifecycleOwner) {
                link = it
                Log.e("testCount", "click screen is  share  and countClickCall is:  $it")

            }

            activity?.let {
                showWebViewDialogScreen(
                    it,
                    it.getString(R.string.url_tips_and_guidance),
                    it.getString(R.string.title_tips_and_guidance)
                )
//                it.getString(R.string.url_tips_and_guidance),

            }
        }
    }

    private fun goToSettings() {
        binding.tvMoreSettings.setOnClickListener {
            val action = MoreFragmentDirections.actionNavigationMoreToSettingsFragment()
            findNavController().navigate(action)

        }


    }


    private fun countClickCall(clickedScreen: String) {

        moreViewModel.sendLinkClick(clickedScreen)

    }
}