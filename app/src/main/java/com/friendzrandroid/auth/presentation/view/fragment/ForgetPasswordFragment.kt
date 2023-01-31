package com.friendzrandroid.auth.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.AuthViewModel
import com.friendzrandroid.auth.presentation.LoginRegisterStatus
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.hideButtonLoading
import com.friendzrandroid.core.utils.isEmailValid
import com.friendzrandroid.core.utils.showButtonLoading
import com.friendzrandroid.databinding.FragmentForgetPasswordBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ForgetPasswordFragment : BaseFragment() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentForgetPasswordBinding.inflate(layoutInflater)
    }
    private val viewModel: AuthViewModel by viewModels()
    override val baseViewModel: BaseViewModel
        get() = viewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        initClicks()
        viewModel.isAuthSuccessful.observe(viewLifecycleOwner) {
            hideLoading()
            when (it) {
                is LoginRegisterStatus.IsSuccessful -> {
                    showToast(getString(R.string.chechMailMessage))
                    binding.btnSend.hideButtonLoading(
                        resources.getString(R.string.title_sent), requireActivity()
                    ) {
                        findNavController().popBackStack()
                    }
                }

                is LoginRegisterStatus.Fail -> {
                    binding.btnSend.showButtonLoading(resources.getString(R.string.send))
                    showToast(it.message)
                }
                else -> {}
            }
        }

//        trackScreenName()

        trackScreenName("Forget Password Screen")
        return binding.root
    }


    private fun initClicks() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSend.setOnClickListener {
            val email = binding.LoginEmailEdt.text.toString()
            if (email.trim().isEmailValid()) {
                //showLoading()
                binding.btnSend.showButtonLoading(resources.getString(R.string.title_sending))
                viewModel.forgetPassword(email)

            } else {
                binding.LoginEmailLayout.error = resources.getString(R.string.error_email)

            }
        }
    }
}