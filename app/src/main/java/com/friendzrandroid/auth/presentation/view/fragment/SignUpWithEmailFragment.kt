package com.friendzrandroid.auth.presentation.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.friendzrandroid.R
import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum
import com.friendzrandroid.auth.presentation.AuthViewModel
import com.friendzrandroid.auth.presentation.LoginRegisterStatus
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.isEmailValid
import com.friendzrandroid.databinding.FragmentSignUpWithEmailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignUpWithEmailFragment : BaseFragment(), View.OnClickListener {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var dialog: Dialog

    private val mBinding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentSignUpWithEmailBinding.inflate(layoutInflater)
    }
    override val baseViewModel: BaseViewModel
        get() = viewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding.btnSignUpFaceBook.setOnClickListener {
            (requireActivity() as AuthActivity).facebookLoginListener(true, viewModel)
        }
        mBinding.btnSignUpGoogle.setOnClickListener {
            (requireActivity() as AuthActivity).googleLoginListener(true, viewModel)
        }


        viewModel.isAuthSuccessful.observe(viewLifecycleOwner) {


            when (it) {
                is LoginRegisterStatus.IsSuccessful -> {
                    hideLoading()

                    congratulationsDialog();

                }

                is LoginRegisterStatus.Fail -> {
                    hideLoading()
                    showToast(it.message)
                }
                else -> {}
            }

        }

        mBinding.signUpBtn.setOnClickListener(this)
        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        trackScreenName("Social media Registration Screen")

        mBinding.textView4.setMovementMethod(LinkMovementMethod.getInstance())


        return mBinding.root
    }


    private fun congratulationsDialog() {

         dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.dialog_success)

        dialog.show()
        val acceptBtn = dialog.findViewById(R.id.btnAcceptRequest) as AppCompatImageButton


        acceptBtn.setOnClickListener {
            clearView()
            dialog.dismiss()
            findNavController().navigate(R.id.action_navigation_to_login)

        }

    }

    private fun clearView() {
        mBinding.userNameEdt.setText("")
        mBinding.passwordEdt.setText("")
        mBinding.confirmPasswordEdt.setText("")
        mBinding.emailEdt.setText("")
        mBinding.signUpBtn.text = resources.getString(R.string.signUp)
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.signUpBtn -> {
                    validateRegisterData()
                }
            }
        }
    }

    private fun validateRegisterData() {

        mBinding.userNameContainer.error = ""
        mBinding.emailContainer.error = ""
        mBinding.passwordContainer.error = ""
        mBinding.confirmPasswordContainer.error = ""


        val userName = mBinding.userNameEdt.text.toString().trim()
        val email = mBinding.emailEdt.text.toString().trim()
        val password = mBinding.passwordEdt.text.toString().trim()
        val confirmPassword = mBinding.confirmPasswordEdt.text.toString().trim()


        if (userName.isNullOrEmpty()) {
            mBinding.userNameContainer.error = resources.getString(R.string.error_username_register)
            return
        }

        if (userName.length > 32 || userName.length < 2) {
            mBinding.userNameContainer.error =
                resources.getString(R.string.error_username_not_valid)
            return
        }

        if (email.isNullOrEmpty() || !email.isEmailValid()) {
            mBinding.emailContainer.error = resources.getString(R.string.error_email)
            return
        }

        if (password.isNullOrEmpty() || password.length < 8) {
            mBinding.passwordContainer.error = resources.getString(R.string.error_password)
            return
        }


        if (!confirmPassword.equals(password)
        ) {
            mBinding.confirmPasswordContainer.error =
                resources.getString(R.string.error_confirm_password)
            return
        }

        showLoading()

        viewModel.performRegister(
            userName,
            email,
            password,
            LoginRegisterTypeEnum.NORMAL,
            userId = "",
            LoginRegisterTypeEnum.PLATFORM_ANDROID
        )

    }

}