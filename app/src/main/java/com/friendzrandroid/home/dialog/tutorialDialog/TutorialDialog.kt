package com.friendzrandroid.home.dialog.tutorialDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.adapters.CompoundButtonBindingAdapter.setChecked
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.google.android.material.bottomnavigation.BottomNavigationView

class TutorialDialog {


    private var context: Context
    private lateinit var dialog: Dialog

    //..we need the context else we can not create the dialog so get context in constructor
    constructor(context: Context) {
        this.context = context
    }


    fun showDialog(screenName: String) {
        var tutorial = screenName
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.instruction_help_dialog)
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //val acceptBtn = dialog.findViewById(R.id.btnAcceptRequest) as AppCompatImageButton


        val dialogImgPrivateModes = dialog.findViewById(R.id.img_privateModes) as SwitchCompat
        val dialogImgMapFilter = dialog.findViewById(R.id.img_mapFilter) as SwitchCompat
        val dialogImgInterestMatch = dialog.findViewById(R.id.img_interestMatch) as SwitchCompat
        val dialogImgCompassButton = dialog.findViewById(R.id.compass_button) as SwitchCompat
        val dialogNextButton = dialog.findViewById(R.id.next_button) as RelativeLayout

        val dialogRlCompassMessageContainer =
            dialog.findViewById(R.id.rl_compassMessageContainer) as RelativeLayout

        val dialogRlCompassMessageContainerText =
            dialog.findViewById(R.id.rl_compassMessageContainerText) as TextView

        val dialogPrivateModeMessageMessageContainer =
            dialog.findViewById(R.id.rl_PrivateModeMessageContainer) as RelativeLayout

        val dialogPrivateModeMessageMessageContainerText =
            dialog.findViewById(R.id.rl_PrivateModeMessageContainerText) as TextView

        val dialogaddNewEventContainer =
            dialog.findViewById(R.id.addNewEvent) as LinearLayout
        val dialogInterestMatchMessageContainer =
            dialog.findViewById(R.id.rl_InterestMatchMessageContainer) as RelativeLayout

        val dialogInterestMatchMessageContainerText =
            dialog.findViewById(R.id.rl_InterestMatchMessageContainerText) as TextView

        val dialogMapInstructionContainer =
            dialog.findViewById(R.id.map_instruction_container) as LinearLayout

        val dialogMapInstructionContainerText =
            dialog.findViewById(R.id.map_instruction_containerText) as TextView

        val dialogMapInstructionNearByEventContainer =
            dialog.findViewById(R.id.map_instruction_nearByEvent_container) as RelativeLayout


        val dialogMapInstructionNearByEventContainerText =
            dialog.findViewById(R.id.map_instruction_nearByEvent_containerText) as TextView


        val dialogMapInstructionNavigationViewContainer =
            dialog.findViewById(R.id.nav_view) as BottomNavigationView

        val dialogMapInstructionEventAroundMeContainer =
            dialog.findViewById(R.id.ll_bottomSheet_event_aroundMe) as LinearLayout

        val dialogRlFeedAppbarContainerContainer =
            dialog.findViewById(R.id.rl_feed_appbarContainer) as RelativeLayout

        val dialogMapCategoryFilterInstructionContainer =
            dialog.findViewById(R.id.rl_MapCategoryFilterMessageContainer) as RelativeLayout

        val dialogMapCategoryFilterInstructionContainerText =
            dialog.findViewById(R.id.rl_MapCategoryFilterMessageContainerText) as TextView





        dialogNextButton.setOnClickListener {
            dialog.dismiss()


        }

        if (tutorial.contains("mapFilterByCategory")) {


//            tutorial = "mapFilterByCategory"
//                dialogImgMapFilter.setChecked(false)


            val token = UserSessionManagement.getKeyAuthToken()

            if (token != null && !token.equals("")) {

            }

            else {

                dialogMapCategoryFilterInstructionContainerText.text =
                    "You can sort filter events by your interests here."
                dialogMapInstructionNearByEventContainerText.text =
                    "You can browse a list of events nearest to you here."
                dialogMapInstructionContainerText.text =
                    "You can add your own event to the map here – inviting your connections or opening to all Friendzrs."


            }


            dialogRlFeedAppbarContainerContainer.hide()

            dialogImgMapFilter.setChecked(false)
            dialogImgMapFilter.show()
            dialogMapCategoryFilterInstructionContainer.show()
            dialogRlFeedAppbarContainerContainer.hide()

            dialogRlCompassMessageContainer.hide()
            dialogPrivateModeMessageMessageContainer.hide()
            dialogInterestMatchMessageContainer.hide()
            dialogMapInstructionContainer.hide()

            dialogaddNewEventContainer.hide()

            Log.d("toturial", "showDialog: mapFilterByCategory")

        } else {

            val token = UserSessionManagement.getKeyAuthToken()

            if (token != null && !token.equals("")) {

            } else {


                dialogInterestMatchMessageContainerText.text =
                    " You can sort the Feed by those who share your interests here."
                dialogPrivateModeMessageMessageContainerText.text =
                    "You can filter the Feed by the groups you want to see (and be seen by)"
                dialogRlCompassMessageContainerText.text =
                    "You can sort the Feed by those nearest to you by pointing the phone in any direction."

            }


        }


        dialogNextButton.setOnClickListener {

            if (tutorial.contains("compass")) {

//                dialogImgMapFilter.setChecked(false)
//                dialogImgCompassButton.setChecked(false)
//                dialogImgInterestMatch.setChecked(false)
//                dialogImgPrivateModes.setChecked(true)

                tutorial = "privateMode"

                dialogRlCompassMessageContainer.hide()
                dialogPrivateModeMessageMessageContainer.show()
                dialogInterestMatchMessageContainer.hide()
                dialogMapInstructionContainer.hide()
                dialogMapCategoryFilterInstructionContainer.hide()
                dialogaddNewEventContainer.hide()


            } else if (tutorial.contains("privateMode")) {
//                dialogImgMapFilter.setChecked(false)
//
//                dialogImgCompassButton.setChecked(false)
//                dialogImgInterestMatch.setChecked(true)
//                dialogImgPrivateModes.setChecked(false)


                tutorial = "interestsMatch"
                dialogRlCompassMessageContainer.hide()
                dialogPrivateModeMessageMessageContainer.hide()
                dialogInterestMatchMessageContainer.show()
                dialogMapInstructionContainer.hide()
                dialogMapCategoryFilterInstructionContainer.hide()
                dialogaddNewEventContainer.hide()


            } else if (tutorial.contains("interestsMatch")) {
//                dialogImgMapFilter.setChecked(false)
//
//                dialogImgCompassButton.setChecked(false)
//                dialogImgInterestMatch.setChecked(false)
//                dialogImgPrivateModes.setChecked(false)

                tutorial = "null"
                dialogRlCompassMessageContainer.hide()
                dialogPrivateModeMessageMessageContainer.hide()
                dialogInterestMatchMessageContainer.hide()
                dialogMapInstructionContainer.hide()
                dialogMapCategoryFilterInstructionContainer.hide()
                dialogaddNewEventContainer.hide()

                dialogRlFeedAppbarContainerContainer.hide()


                val token = UserSessionManagement.getKeyAuthToken()

                if (token != null && !token.equals("")) {
                    UserSessionManagement.changeFirstOpenFeedWithToken()
                } else {


                    UserSessionManagement.changeFirstOpenFeed()
                }


                hideDialog()

            } else if (tutorial.contains("mapFilterByCategory")) {
                Log.d("toturial", "showDialog: mapFilterByCategory")

                tutorial = "nearByEvent"

                dialogMapInstructionEventAroundMeContainer.show()
                dialogMapInstructionNearByEventContainer.show()
                dialogImgMapFilter.hide()
                dialogRlFeedAppbarContainerContainer.hide()
                dialogRlCompassMessageContainer.hide()
                dialogPrivateModeMessageMessageContainer.hide()
                dialogInterestMatchMessageContainer.hide()
                dialogMapCategoryFilterInstructionContainer.hide()
                dialogMapInstructionContainer.hide()
                dialogaddNewEventContainer.hide()







//                UserSessionManagement.changeFirstOpenMap()


            } else if (tutorial.contains("nearByEvent")) {

                tutorial = "mapAddEvent"
                dialogaddNewEventContainer.show()
                dialogMapInstructionContainer.show()

                dialogImgMapFilter.hide()

                dialogRlFeedAppbarContainerContainer.hide()
                dialogRlCompassMessageContainer.hide()
                dialogPrivateModeMessageMessageContainer.hide()
                dialogInterestMatchMessageContainer.hide()

                dialogMapCategoryFilterInstructionContainer.hide()

                dialogImgMapFilter.hide()
                dialogMapCategoryFilterInstructionContainer.hide()

                dialogMapInstructionEventAroundMeContainer.visibility=View.INVISIBLE
                dialogMapInstructionNearByEventContainer.hide()


                Log.d("toturial", "showDialog: mapAddEvent")

            } else if (tutorial.contains("nearByEvent")) {

                tutorial = "null"


                dialogaddNewEventContainer.show()
                dialogMapInstructionContainer.show()

                dialogImgMapFilter.hide()

                dialogRlFeedAppbarContainerContainer.hide()
                dialogRlCompassMessageContainer.hide()
                dialogPrivateModeMessageMessageContainer.hide()
                dialogInterestMatchMessageContainer.hide()

                dialogMapCategoryFilterInstructionContainer.hide()

                dialogImgMapFilter.hide()
                dialogMapCategoryFilterInstructionContainer.hide()
                dialogMapInstructionEventAroundMeContainer.visibility=View.INVISIBLE
                dialogMapInstructionNearByEventContainer.hide()


                val token = UserSessionManagement.getKeyAuthToken()

                if (token != null && !token.equals("")) {

                    UserSessionManagement.changeFirstOpenMapWithToken()
                } else {
                    UserSessionManagement.changeFirstOpenMap()
                }




                Log.d("toturial", "showDialog: nearByEvent")
                hideDialog()
//                hideDialog()
            } else {
                Log.d("toturial", "showDialog: else")

                hideDialog()
            }


        }


        dialog.show()
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        if (this::dialog.isInitialized && dialog.isShowing)
            dialog.dismiss()
        UserSessionManagement.changeFirstOpenFeed()

    }
}

