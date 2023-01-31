package com.friendzrandroid.home.dialog.gostModeDialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.utils.UserSessionManagement

class AllowMyApperanceDialog {

    private val TAG = "AllowMyApperanceDialog"

    private var context: Context
    private lateinit var dialog: Dialog

    private lateinit var everyOneCheckBox: CheckBox
    private lateinit var menCheckBox: CheckBox
    private lateinit var womenCheckBox: CheckBox
    private lateinit var otherGenderCheckBox: CheckBox

    //..we need the context else we can not create the dialog so get context in constructor
    constructor(context: Context) {
        this.context = context
    }


    fun showDialog(listener: (ArrayList<Int>) -> Unit) {
        var checkBoxNumber: Int = 0
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(true)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.dialog_gost_mode)
        val window = dialog.window


        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        everyOneCheckBox = dialog.findViewById(R.id.everyOneBtn) as CheckBox
        menCheckBox = dialog.findViewById(R.id.menBtn) as CheckBox
        womenCheckBox = dialog.findViewById(R.id.womenBtn) as CheckBox
        otherGenderCheckBox = dialog.findViewById(R.id.otherGenderBtn) as CheckBox

        val savePrivateModeBtn = dialog.findViewById(R.id.savePrivateModeBtn) as Button

        val hideFrom: ArrayList<Int> = ArrayList(0)

        everyOneCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->


//            compoundButton.isChecked=isChecked


            if (isChecked){
                menCheckBox.isChecked = false
                womenCheckBox.isChecked = false
                otherGenderCheckBox.isChecked = false
                compoundButton.isChecked=true

//                setModeCheckBoxState(true, checkBoxNumber)

                checkBoxNumber = 0

            }else{

                compoundButton.isChecked=false
//                checkBoxNumber = 0
//                setModeCheckBoxState(false, checkBoxNumber)


            }
//            compoundButton.isChecked = isChecked



        }

        menCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->
//            compoundButton.isChecked = isChecked


            if (isChecked){
            if (checkBoxNumber<2){
                checkBoxNumber += 1
//                setModeCheckBoxState(false, checkBoxNumber)

//                menCheckBox.isChecked = false
//                womenCheckBox.isChecked = false
//                otherGenderCheckBox.isChecked = false
                compoundButton.isChecked=true
                everyOneCheckBox.isChecked = false



            }else{
                compoundButton.isChecked=false
//                checkBoxNumber -= 1
//                setModeCheckBoxState(false, checkBoxNumber)



            }}else{
                checkBoxNumber -= 1
                compoundButton.isChecked=false

//                setModeCheckBoxState(false, checkBoxNumber)


            }


        }

        womenCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->
//            compoundButton.isChecked = isChecked

            if (isChecked){
            if (checkBoxNumber<2){
                compoundButton.isChecked=true
                everyOneCheckBox.isChecked = false

                checkBoxNumber += 1
//                setModeCheckBoxState(false, checkBoxNumber)




            }else{
                compoundButton.isChecked=false
//                setModeCheckBoxState(false, checkBoxNumber)


            }
            }else{
                checkBoxNumber -= 1
                compoundButton.isChecked=false

//                setModeCheckBoxState(false, checkBoxNumber)

            }


//            setModeCheckBoxState(false, checkBoxNumber)

        }

        otherGenderCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->
//            compoundButton.isChecked = isChecked

            if (isChecked){
            if (checkBoxNumber<2){
                compoundButton.isChecked=true
                everyOneCheckBox.isChecked = false

                checkBoxNumber += 1
//                setModeCheckBoxState(false, checkBoxNumber)


            }else{
                compoundButton.isChecked=false
//                setModeCheckBoxState(false, checkBoxNumber)

//                checkBoxNumber -= 1


            }
            }else{
                checkBoxNumber -= 1
//                setModeCheckBoxState(false, checkBoxNumber)
                compoundButton.isChecked=false

            }
//            setModeCheckBoxState(false, checkBoxNumber)


        }

        val checkBoxList = listOf(everyOneCheckBox, menCheckBox, womenCheckBox, otherGenderCheckBox)
        savePrivateModeBtn.setOnClickListener {
            val token = UserSessionManagement.getKeyAuthToken()

            if (token != null && !token.equals("")) {

                hideFrom.clear()
                checkBoxList.forEachIndexed { index, checkBox ->
                    if (checkBox.isChecked)
                        hideFrom.add(index + 1)
                }

                listener(hideFrom)
                dialog.dismiss()
                Log.i(TAG, "HideFrom: $hideFrom")

            } else {

                context.startActivity(Intent(context, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }

        dialog.setOnDismissListener {
            listener(hideFrom)
            dialog.dismiss()
        }
        dialog.show()
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        if (this::dialog.isInitialized && dialog.isShowing)
            dialog.dismiss()
        dialog.cancel()
    }

    private fun setModeCheckBoxState(isEveryOne: Boolean, checkBoxNumber: Int) {
        if (isEveryOne) {
                menCheckBox.isChecked = false
                womenCheckBox.isChecked = false
                otherGenderCheckBox.isChecked = false
            } else
                everyOneCheckBox.isChecked = false

    }

}