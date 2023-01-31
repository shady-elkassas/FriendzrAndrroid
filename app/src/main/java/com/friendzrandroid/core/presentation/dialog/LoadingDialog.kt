package com.friendzrandroid.core.presentation.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import com.friendzrandroid.R

class LoadingDialog {
    private lateinit var context: Context
    private lateinit var dialog: Dialog
//    R.style.baseDialog
    constructor(context: Context) {
        this.context = context
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.dialog_loading)
        val window = dialog.window
        window?.setBackgroundDrawable(context.resources.getDrawable(R.color.white_f5))
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window?.setStatusBarColor(context.resources.getColor(R.color.white_f5))
        }

        // Initialize lottie loading animation
//        val lottieAnimationView =  dialog.findViewById<ProgressBar>(R.id.animationView)
//        lottieAnimationView.

    }

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    fun showDialog() {

        //...finaly show it
        dialog.show()
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        if (this::dialog.isInitialized && dialog.isShowing)
            dialog.dismiss()
    }
}