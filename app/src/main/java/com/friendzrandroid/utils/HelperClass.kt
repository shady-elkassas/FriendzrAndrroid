package com.friendzrandroid.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.*
import androidx.core.view.isVisible
import com.friendzrandroid.databinding.DialogWebViewBinding
import com.friendzrandroid.home.adapter.listener.TicketDetailsListener


object HelperClass : Activity() {


//    fun showSignUpDialogScreen(activity: Activity): Dialog {
//        val dialog = Dialog(activity)
//
//        val binding = DialogSignUpBinding.inflate(LayoutInflater.from(activity))
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(binding.root)
//
//        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow()
//            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        binding.ivBack.setOnClickListener { dialog.dismiss() }
//        binding.btnSignupSignup.setOnClickListener {
//            dialog.dismiss()
//            startActivity(Intent(activity, MainActivity::class.java))
//            activity.finish()
//
//        }
//        dialog.show()
//        return dialog
//    }

    fun showWebViewExternalEventDialogScreen(
        activity: Activity,
        websiteUrl: String,
        screenTitle: String,
        listener: TicketDetailsListener
    ): Dialog {
        val dialog = Dialog(activity)
        val binding = DialogWebViewBinding.inflate(LayoutInflater.from(activity))

        val height = (activity.resources.displayMetrics.heightPixels * 0.70).toInt()
        val width = (activity.resources.displayMetrics.widthPixels * 0.90).toInt()


        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.root)

        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.setLayout(width, height)
        binding.appbarTitle.setText(screenTitle)
        binding.imgBack.setOnClickListener {
            dialog.dismiss()
            listener.onTicketWebClosed(true)
        }

        setWebViewImplementation(binding.webView, websiteUrl, activity, binding)
        dialog.show()
        return dialog
    }


    fun showWebViewDialogScreen(
        activity: Activity,
        websiteUrl: String,
        screenTitle: String
    ): Dialog {
        val dialog = Dialog(activity)
        val binding = DialogWebViewBinding.inflate(LayoutInflater.from(activity))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.root)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding.appbarTitle.setText(screenTitle)
        binding.imgBack.setOnClickListener { dialog.dismiss() }
        setWebViewImplementation(binding.webView, websiteUrl, activity, binding)
        dialog.show()

        return dialog
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewImplementation(
        webView: WebView,
        websiteUrl: String,
        activity: Activity,
        binding: DialogWebViewBinding
    ) {


//        val settings = webView.getSettings()
//
//        settings.setJavaScriptEnabled(true);

        webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY;
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportMultipleWindows(true)

        webView.webViewClient = object : WebViewClient() {


            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {


                view!!.loadUrl(websiteUrl);


                return true
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (binding.progressBar.isVisible) {
                    binding.progressBar.visibility = View.GONE
                }

            }


            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    Toast.makeText(
//                        activity,
//                        "Error:" + error?.description.toString(),
//                        Toast.LENGTH_LONG
//                    )
//                        .show()
//                } else {
//
//                    Toast.makeText(
//                        activity,
//                        "Error: Please try again!",
//                        Toast.LENGTH_LONG
//                    )
//                }

            }


        }


        webView.loadUrl(websiteUrl)


    }


}

