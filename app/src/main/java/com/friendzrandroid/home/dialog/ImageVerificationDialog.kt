package com.friendzrandroid.home.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.loadImage

class ImageVerificationDialog {
    private var context: Context
    private var selectedImage: String = ""
    private lateinit var dialog: Dialog

    //..we need the context else we can not create the dialog so get context in constructor
    constructor(context: Context, imageSelected: String) {
        this.context = context
        this.selectedImage = imageSelected
    }


    fun showDialog(listener: (Int) -> Unit) {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(true)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.dialog_verifiy_profile_image)
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val verifyBtn = dialog.findViewById(R.id.verify_button) as Button
        val image = dialog.findViewById(R.id.image_uploaded) as ImageView
        image.loadImage(selectedImage)

        verifyBtn.setOnClickListener {
            dialog.dismiss()
            listener(1)
        }

        dialog.show()
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        if (this::dialog.isInitialized && dialog.isShowing)
            dialog.dismiss()
    }


}