package com.friendzrandroid.core.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.presentation.dialog.LoadingDialog
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.showToast
import com.friendzrandroid.home.dialog.NoConnectionDialog
import com.friendzrandroid.home.dialog.RefreshCallBack
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

abstract class BaseFragment : Fragment(), RefreshCallBack {

    abstract val baseViewModel: BaseViewModel
    private lateinit var progessDialog: LoadingDialog
    lateinit var noConnectionDialog: NoConnectionDialog

    private fun observeBaseViewModel() {
        baseViewModel.let {
            it.errorMessage.observe(this, Observer {
                hideLoading()
                //baseContext.showDialogMessage(it)

                showToast(it)
                //Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })

            it.networkError.observe(this, Observer {
                hideLoading()
                showNoConnectionDialog()

            })
            it.serverError.observe(this, Observer {
                hideLoading()
//                showServerDownDialog()

                showToast("Server error")
                //Toast.makeText(requireContext(), "server error", Toast.LENGTH_SHORT).show()

            })
            it.authorizationError.observe(this, Observer {
                hideLoading()
                UserSessionManagement.removeUserSession()
                startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                })
            })
        }
    }

     fun trackScreenName(screenName:String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)


    }
    override fun onResume() {
        super.onResume()
        observeBaseViewModel()
    }

    fun showLoading() {
        if (!this::progessDialog.isInitialized)
            progessDialog = LoadingDialog(requireContext())
        progessDialog.showDialog()
    }

    fun hideLoading() {
        if (this::progessDialog.isInitialized) {
            progessDialog.hideDialog()
        }
    }


    fun showToast(@StringRes id: Int) {
        Toast(requireContext()).showToast(activity, id.toString())
    }


    fun showToast(text: String) {
        Toast(requireContext()).showToast(activity, text)
    }


    fun showNoConnectionDialog() {
        if (!this::noConnectionDialog.isInitialized)
            noConnectionDialog = NoConnectionDialog(requireContext(), this)
        noConnectionDialog.showDialog()
    }


    fun hideNoConnectionDialog() {
        if (this::noConnectionDialog.isInitialized)
            noConnectionDialog.hideDialog()
    }

    override fun onRefresh() {
        hideNoConnectionDialog()
    }
}