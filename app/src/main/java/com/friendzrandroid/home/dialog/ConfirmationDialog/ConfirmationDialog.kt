package com.friendzrandroid.home.dialog.ConfirmationDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.cardview.widget.CardView
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show

class ConfirmationDialog {


    private var context: Context
    private var confirmationMessage: String = ""
    private var editButton: Boolean = true
    private lateinit var dialog: Dialog

    //..we need the context else we can not create the dialog so get context in constructor
    constructor(context: Context, confirmationMessage: String, editButton: Boolean) {
        this.context = context
        this.confirmationMessage = confirmationMessage
        this.editButton = editButton
    }


    fun showDialog(listener: (Int) -> Unit) {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(true)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.dialog_confirmation)
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        val acceptBtn = dialog.findViewById(R.id.btnAcceptRequest) as AppCompatImageButton
        val editBtn = dialog.findViewById(R.id.btn_edit) as Button
        val closeBtn = dialog.findViewById(R.id.btn_close) as Button

        val dialogConfirmationMessage = dialog.findViewById(R.id.tv_headerSubTitle) as TextView

        dialogConfirmationMessage.setText(confirmationMessage)

        val cancelBtn = dialog.findViewById(R.id.btnDeclineRequest) as AppCompatImageButton


        if (editButton == false) {
            editBtn.show()
            closeBtn.show()
            acceptBtn.hide()
            cancelBtn.hide()

        }else{
            editBtn.hide()
            closeBtn.hide()

        }

        acceptBtn.setOnClickListener {
            dialog.dismiss()
            listener(1)
        }


        editBtn.setOnClickListener {
            dialog.dismiss()
            listener(3)
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
            listener(1)

        }


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