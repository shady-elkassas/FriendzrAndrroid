package com.friendzrandroid.core.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.friendzrandroid.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SelectImageUtil(private val activity: Fragment, private val response: MutableLiveData<Uri>) {

    var fullScreen: Boolean = false

    // step 1 - upload image
    fun selectImage(fullScreen: Boolean) {

        if (ContextCompat.checkSelfPermission(
                activity.requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.fullScreen = fullScreen

            requestPermission.launch(Manifest.permission.CAMERA)


        } else {
            showPictureDialog(fullScreen)

        }
    }


    fun openCamera(isFaceVerification: Boolean = false) {
        if (ContextCompat.checkSelfPermission(
                activity.requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            requestPermission.launch(Manifest.permission.CAMERA)


        } else {

            if (isFaceVerification)
                captureImageFullSize()
            else
                captureImageWithAspectRatio()
        }
    }

    // step 2 - upload image
    private fun showPictureDialog(fullScreen: Boolean) {

        MaterialAlertDialogBuilder(activity.requireContext())
            .setTitle(activity.resources.getString(R.string.select_image))
            .setItems(R.array.image_options) { dialog, which ->
                when (which) {

                    0 -> {
                        if (fullScreen) {
                            openGalleryForImageFullSize()
                            Log.d("selectImage", "showPictureDialog:openGalleryForImageFullSize ")
                        } else {
                            openGalleryForImage()
                            Log.d("selectImage", "showPictureDialog:openGalleryForImage ")

                        }
                    }

                    1 -> {
                        if (fullScreen) {
                            captureImageFullSize()
                            Log.d("selectImage", "showPictureDialog:captureImageFullSize ")

                        } else {
                            captureImageWithAspectRatio()
                            Log.d("selectImage", "showPictureDialog:captureImageWithAspectRatio ")


                        }

                    }
                }

            }
            .setNegativeButton(activity.resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press
                dialog.cancel()
            }
            .show()
    }

    // step 3 - upload image
    private fun openGalleryForImage() {
        ImagePicker.with(activity)
            .crop(3f, 2f)
            .galleryOnly()//Crop image(Optional), Check Customization for more option

            .compress(1024)
            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent {
                getResults.launch(it)
            }
    }


    private fun openGalleryForImageFullSize() {
        ImagePicker.with(activity)
            .crop()
            .galleryOnly()//Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent {
                getResults.launch(it)
            }
    }


    private fun captureImageWithAspectRatio() {
        ImagePicker.with(activity)
            .crop(3f, 2f)
            .cameraOnly()
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ).createIntent {
                getResults.launch(it)
            }
    }

    private fun captureImageFullSize() {
        ImagePicker.with(activity)
            .crop()
            .cameraOnly()
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ).createIntent {
                getResults.launch(it)
            }
    }


    private val requestPermission =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showPictureDialog(fullScreen)
            }

        }


    private val getResults =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = result.data?.data
                response.value = fileUri!!
//                file = File(fileUri!!.path)
//
//                binding.imgUserProfile.loadImage(fileUri)

            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast(activity.requireContext()).showToast(
                    activity.requireActivity(),
                    ImagePicker.getError(result.data)
                )

//                Toast.makeText(
//                    activity.requireContext(),
//                    ImagePicker.getError(result.data),
//                    Toast.LENGTH_SHORT
//                ).show()
            } else {
//                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }

        }


}