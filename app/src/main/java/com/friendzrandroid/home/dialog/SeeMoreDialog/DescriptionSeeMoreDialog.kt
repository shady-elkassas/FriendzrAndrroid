package com.friendzrandroid.home.dialog.SeeMoreDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.ScrollingMovementMethod
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.friendzrandroid.R

class DescriptionSeeMoreDialog {


    private var context: Context
    private var confirmationMessage: String = ""
    private lateinit var dialog: Dialog

    //..we need the context else we can not create the dialog so get context in constructor
    constructor(context: Context, confirmationMessage: String) {
        this.context = context
        this.confirmationMessage = confirmationMessage
    }


    fun showDialog(listener: (Int) -> Unit) {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(true)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.dialog_see_more)
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //val acceptBtn = dialog.findViewById(R.id.btnAcceptRequest) as AppCompatImageButton

        val dialogConfirmationMessage = dialog.findViewById(R.id.tv_headerSubTitle) as TextView

        dialogConfirmationMessage.movementMethod = ScrollingMovementMethod()
        dialogConfirmationMessage.setText(confirmationMessage)

        val cancelBtn = dialog.findViewById(R.id.close_dialog) as ImageView


//        acceptBtn.setOnClickListener {
//            dialog.dismiss()
//            listener(1)
//        }
//
//
        cancelBtn.setOnClickListener {
            dialog.dismiss()
            listener(0)
        }
        dialog.show()
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        if (this::dialog.isInitialized && dialog.isShowing)
            dialog.dismiss()
    }
}

