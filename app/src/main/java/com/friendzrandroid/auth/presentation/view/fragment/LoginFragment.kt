package com.friendzrandroid.auth.presentation.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.friendzrandroid.R
import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum
import com.friendzrandroid.auth.presentation.AuthViewModel
import com.friendzrandroid.auth.presentation.LoginRegisterStatus
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.hideButtonLoading
import com.friendzrandroid.core.utils.isEmailValid
import com.friendzrandroid.core.utils.showButtonLoading
import com.friendzrandroid.databinding.FragmentLoginBinding
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.fragment.whiteLabel.CommunityActivity
import com.friendzrandroid.home.fragment.whiteLabel.WhiteLabelActivityActivity
import com.friendzrandroid.splash.presentation.activity.IntroActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : BaseFragment() {


    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: AuthViewModel by viewModels()


    override val baseViewModel: BaseViewModel
        get() = viewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        viewModel.isAuthSuccessful.observe(viewLifecycleOwner) {


            when (it) {
                is LoginRegisterStatus.IsSuccessful -> {

                    hideLoading()

                    binding.btnLoginNormal.hideButtonLoading("Success", requireActivity()) {
                        startActivity(
                            Intent(
                                requireContext(),
                                MainActivity::class.java
                            ).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })

                    }

                }

                is LoginRegisterStatus.IsWhiteLabel -> {

                    hideLoading()

                   // here stop navigation and show toast with please enter another mail
                    binding.btnLoginNormal.hideButtonLoading("Success", requireActivity()) {

                        startActivity(
                            Intent(
                                requireContext(),
                                CommunityActivity::class.java
                            ).putExtra("isWhiteLabel", true).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })


//                        findNavController().navigate(LoginFragmentDirections.actionFragmentLoginToNavigationInbox())


                    }

                }

                is LoginRegisterStatus.needToUpdate -> {

                    hideLoading()

                    //                  LoginFragmentDirections.actionFragmentLoginToEditProfileFragment(
//                               true,
//                               it.userData
//                           )


                    // call intro screen first and then call edit profile
//                    findNavController().navigate(LoginFragmentDirections.actionFragmentLoginToIntro1PeopleFragment())

                    binding.btnLoginNormal.hideButtonLoading("Success", requireActivity()) {
                        startActivity(
                            Intent(
                                requireContext(),
                                IntroActivity::class.java
                            ).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })

                    }

                }

                is LoginRegisterStatus.Fail -> {
                    hideLoading()
                    binding.btnLoginNormal.showButtonLoading(resources.getString(R.string.log_in))
                    showToast(it.message)
                }
                else -> {
                    showToast("Please try again")

                }
            }

        }




        initClicks()
        trackScreenName("Login Screen")

        return binding.root
    }

    private fun initClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnLoginNormal.setOnClickListener {
            letsLogin()
        }

        binding.btnForgetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_to_forgetPassword)
        }

        binding.btnBackToSignUp.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSignWithFaceBook.setOnClickListener {
            (requireActivity() as AuthActivity).facebookLoginListener(true, viewModel)
        }
        binding.btnSignWithGoogle.setOnClickListener {
            (requireActivity() as AuthActivity).googleLoginListener(true, viewModel)

        }
    }

    fun letsLogin() {

        binding.edtLoginMailContainer.error = ""
        binding.edtLayoutLoginPasswordValueContainer.error = ""

        val email = binding.edtLoginMail.text.toString().trim()
        val password = binding.edtLoginPasswordValue.text.toString().trim()

        if (!email.isEmailValid()) {
            binding.edtLoginMailContainer.error = resources.getString(R.string.error_email)
            return
        }
        if (password.trim().length < 8) {
            binding.edtLayoutLoginPasswordValueContainer.error =
                resources.getString(R.string.error_password)
            return
        }


        //showLoading()
        binding.btnLoginNormal.showButtonLoading("Logging in...")
        viewModel.performLogin(
            email,
            password,
            LoginRegisterTypeEnum.NORMAL,
            "",
            LoginRegisterTypeEnum.ANDROID
        )

    }


}