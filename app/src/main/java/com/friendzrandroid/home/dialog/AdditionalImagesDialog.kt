package com.friendzrandroid.home.dialog

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendzrandroid.R
import com.friendzrandroid.home.adapter.AdditionalImagesAdapter


class AdditionalImagesDialog(lisener: AddImagesDialogListener) {


    private var rvAddAdditionalImages: RecyclerView? =null

    //    private lateinit var rvAddAdditionalImages: RecyclerView
    private lateinit var dialog: Dialog
    var lisener = lisener

    //..we need the context else we can not create the dialog so get context in constructor


    fun showDialog(requireContext: Context,uris: List<Uri>?=null) {

        dialog = Dialog(requireContext, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        //...set cancelable false so that it's never get hidden
//        dialog.setCancelable(false)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.add_aditional_images)
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //val acceptBtn = dialog.findViewById(R.id.btnAcceptRequest) as AppCompatImageButton


//      val dialogImgPrivateModes = dialog.findViewById(R.id.img_privateModes) as SwitchCompat
//        val dialogImgMapFilter = dialog.findViewById(R.id.img_mapFilter) as SwitchCompat
        rvAddAdditionalImages = dialog.findViewById(R.id.rv_add_additional_images) as RecyclerView

        val backButton = dialog.findViewById(R.id.btnBack) as ImageView
        val addImagesButton = dialog.findViewById(R.id.tv_add_additional_images) as TextView



        var adaptor = uris?.let { AdditionalImagesAdapter(it) }
        rvAddAdditionalImages!!.setLayoutManager(GridLayoutManager(requireContext, 2))

        rvAddAdditionalImages!!.setAdapter(adaptor)
        if (adaptor != null) {
            adaptor.notifyDataSetChanged()
        }


        backButton.setOnClickListener {
//            dialog.dismiss()

            hideDialog()
        }



        addImagesButton.setOnClickListener {
            lisener.onAddImages()


        }


        dialog.show()
    }

    fun setUpRecyclerView(uris: List<Uri>?, requireContext: Context) {
        var adaptor = uris?.let { AdditionalImagesAdapter(it) }

        rvAddAdditionalImages!!.setAdapter(adaptor)
        if (adaptor != null) {
            adaptor.notifyDataSetChanged()
        }
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        if (this::dialog.isInitialized && dialog.isShowing)
            dialog.hide()

    }


}

