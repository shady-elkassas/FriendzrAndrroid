package com.friendzrandroid.core.presentation.ui

import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.auth.presentation.view.fragment.LoginFragment
import com.friendzrandroid.core.presentation.dialog.LoadingDialog
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.clearStack
import com.friendzrandroid.core.utils.showToast

abstract class BaseActivity : AppCompatActivity() {

    abstract val baseViewModel: BaseViewModel?
    private lateinit var progessDialog: LoadingDialog

    private fun observeBaseViewModel() {
        baseViewModel?.let {
            it.errorMessage.observe(this, Observer {
                hidLoading()
                //baseContext.showDialogMessage(it)

                showToast(it)
                //Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            })
            it.networkError.observe(this, Observer {
                hidLoading()

                showToast("Network error")
                //Toast.makeText(this, "network error", Toast.LENGTH_SHORT).show()

            })
            it.serverError.observe(this, Observer {
                hidLoading()

                showToast("Server error")
                //Toast.makeText(this, "server error", Toast.LENGTH_SHORT).show()

            })
            it.badRequestError.observe(this, Observer {
                hidLoading()

                showToast("Bad request")
                //Toast.makeText(this, "Bad Request", Toast.LENGTH_SHORT).show()

            })

            it.authorizationError.observe(this, Observer {
                hidLoading()
                UserSessionManagement.removeUserSession()
                startActivity(Intent(this, AuthActivity::class.java).clearStack())

            })
        }
    }

    override fun onResume() {
        super.onResume()
        observeBaseViewModel()
    }

    fun showToast(@StringRes id: Int) {
        Toast(this).showToast(this, id.toString())
    }


    fun showToast(text: String) {
        Toast(this).showToast(this, text)
    }

    fun showLoading() {
        if (!this::progessDialog.isInitialized)
            progessDialog = LoadingDialog(this);
        progessDialog.showDialog()
    }

    fun hidLoading() {
        if (this::progessDialog.isInitialized) {
            progessDialog.hideDialog()
        }
    }


}