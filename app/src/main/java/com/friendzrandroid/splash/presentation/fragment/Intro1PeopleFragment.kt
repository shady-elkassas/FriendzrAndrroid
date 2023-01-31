package com.friendzrandroid.splash.presentation.fragment

import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import com.friendzrandroid.databinding.FragmentIntro1PeopleBinding


class Intro1PeopleFragment : Fragment() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentIntro1PeopleBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        openLogoView()

        return binding.root
    }


    private fun openLogoView() {


        val filePlace =
            "android.resource://" + context!!.packageName + "/raw/" + com.friendzrandroid.R.raw.tutorial1

        binding.gifImageView.setVideoURI(Uri.parse(filePlace))
        binding.gifImageView.requestFocus()


        val surfaceholder: SurfaceHolder = binding.gifImageView.getHolder()
        surfaceholder.setFormat(PixelFormat.TRANSPARENT)

//        binding.gifImageView.seekTo(300)
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



