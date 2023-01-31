package com.friendzrandroid.splash.presentation.activity

import android.R
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.databinding.ActivityLandinPageBinding
import com.friendzrandroid.home.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi


class LandinPageActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLandinPageBinding.inflate(layoutInflater)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        openLogoView()





        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        binding.btnTakeATour.setOnClickListener {

            startActivity(Intent(this@LandinPageActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            })

        }




        binding.tvSignupGeneralLogin.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java).putExtra("landingPage", 1).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

    }

    private fun openLogoView() {
        val filePlace =
            "android.resource://" + packageName + "/raw/" + com.friendzrandroid.R.raw.logo_gif_video

        binding.gifImageView.setVideoURI(Uri.parse(filePlace))
        binding.gifImageView.requestFocus()


        val surfaceholder: SurfaceHolder = binding.gifImageView.getHolder()
        surfaceholder.setFormat(PixelFormat.TRANSPARENT)

        binding.gifImageView.seekTo(300)
        binding.gifImageView.setZOrderOnTop(true)

        binding.gifImageView.start()


        binding.gifImageView.setOnPreparedListener { mp ->
            binding.gifImageView.setZOrderOnTop(true)
//            mp.isLooping = true // Loops the video
        }


    }

    override fun onResume() {
        super.onResume()

        openLogoView()

    }
}