package com.friendzrandroid.home.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.friendzrandroid.R

class NoConnectionDialog {


    private var context: Context
    private lateinit var dialog: Dialog
    private var refreshCallBack: RefreshCallBack
    lateinit  var refreshBtn : CardView

    //..we need the context else we can not create the dialog so get context in constructor
    constructor(context: Context, refreshCallBack: RefreshCallBack) {
        this.context = context
        this.refreshCallBack = refreshCallBack

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))



        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.dialog_no_internet_connection)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = Color.WHITE
        }


        refreshBtn = dialog.findViewById(R.id.tryAgainBtn) as CardView

        refreshBtn.setOnClickListener { refreshCallBack.onRefresh() }
    }

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



interface RefreshCallBack {
    fun onRefresh()
}