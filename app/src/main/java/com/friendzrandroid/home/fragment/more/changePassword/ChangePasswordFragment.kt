package com.friendzrandroid.home.fragment.more.changePassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.hideButtonLoading
import com.friendzrandroid.core.utils.showButtonLoading
import com.friendzrandroid.databinding.FragmentChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment() {


    private val binding: FragmentChangePasswordBinding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChangePasswordBinding.inflate(layoutInflater)
    }

    private val viewModel: ChangePasswordViewModel by viewModels()
    override val baseViewModel: BaseViewModel
        get() = viewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.btnChangePassword.setOnClickListener {
            checkPassword()
        }
        viewModel.failMessage.observe(viewLifecycleOwner) {
            binding.btnChangePassword.showButtonLoading(resources.getString(R.string.send))
            showToast(it)
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.changePasswordState.observe(viewLifecycleOwner) {
            if (it)
                binding.btnChangePassword.hideButtonLoading("Success", requireActivity()) {
                    findNavController().popBackStack()
                }
        }

        return binding.root
    }


    fun checkPassword() {
        binding.oldPasswordLayout.error = ""
        binding.newPasswordLayout.error = ""
        binding.confirmPasswordLayout.error = ""
        val oldPassword = binding.oldPasswordEdt.text.toString()
        val newPassword = binding.newPasswordEdt.text.toString()
        val confirmPassword = binding.confirmPasswordEdt.text.toString()

        if (oldPassword.isEmpty()) {
            binding.oldPasswordLayout.error = "Enter current password"
            return
        }

        if (newPassword.isEmpty()) {
            binding.newPasswordLayout.error = "Enter new password"
            return
        }
        if (newPassword.length < 8) {
            binding.newPasswordLayout.error = resources.getString(R.string.error_password)
            return
        }
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordLayout.error = "Enter confirm password"
            return
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.confirmPasswordLayout.error = "Confirm password not equal new password"
            return
        }

        //showLoading()
        binding.btnChangePassword.showButtonLoading(resources.getString(R.string.title_saving))
        viewModel.changePassword(newPassword, oldPassword)
    }

}