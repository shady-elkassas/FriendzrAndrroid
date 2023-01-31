package com.friendzrandroid.auth.presentation.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.auth.presentation.AuthViewModel
import com.friendzrandroid.auth.presentation.LoginRegisterStatus
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.databinding.FragmentSignupBinding
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.splash.presentation.activity.IntroActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignupFragment : BaseFragment() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentSignupBinding.inflate(layoutInflater)
    }


    private val viewModel: AuthViewModel by viewModels()


    override val baseViewModel: BaseViewModel
        get() = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


     /*   if (!UserSessionManagement.getKeyAuthToken()
                .isNullOrEmpty()
        ) {
            if (UserSessionManagement.userNeedToUpdate() == NeedToUpdateStatus.UPDATE_PROFILE.status) {

                findNavController().navigate(R.id.editProfileFragment)


            } else {
                startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }*/



        viewModel.isAuthSuccessful.observe(viewLifecycleOwner, {


            when (it) {

                is LoginRegisterStatus.IsSuccessful -> {

                    hideLoading()

                    startActivity(
                        Intent(
                            requireContext(),
                            MainActivity::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })

                }
                is LoginRegisterStatus.needToUpdate -> {

                    hideLoading()



//                    startActivity(
//                        Intent(
//                            requireContext(),
//                            MainActivity::class.java
//                        ).putExtra("needToUpdate", UserSessionManagement.userNeedToUpdate()).apply {
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        })


                    startActivity(
                        Intent(
                            requireContext(),
                            IntroActivity::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })

                }

                is LoginRegisterStatus.Fail -> {
                    hideLoading()
                    showToast(it.message)
                }
                else -> {}
            }

        })
        initClicks()

        trackScreenName("Registration Screen")

        return binding.root
    }

    private fun initClicks() {

        binding.btnSignupWithmail.setOnClickListener {
            findNavController().navigate(
                SignupFragmentDirections.actionNavigationToSignupWithEmail()
            )

        }
        binding.tvSignupGeneralLogin.setOnClickListener {
            findNavController().navigate(
                SignupFragmentDirections.actionNavigationToLogin()
            )

        }

        binding.btnSignUpFaceBook.setOnClickListener {
            (requireActivity() as AuthActivity).facebookLoginListener(true, viewModel)
        }
        binding.btnSignUpGoogle.setOnClickListener {
            (requireActivity() as AuthActivity).googleLoginListener(true, viewModel)

        }
    }


}