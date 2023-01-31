package com.friendzrandroid.splash.presentation.fragment

import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import com.friendzrandroid.R
import com.friendzrandroid.databinding.FragmentIntro3Binding
import com.friendzrandroid.databinding.FragmentIntro5Binding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Intro5Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Intro5Fragment : Fragment() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentIntro5Binding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        openLogoView()

        return binding.root
    }


    private fun openLogoView() {



        val filePlace =
            "android.resource://" + context!!.packageName + "/raw/" + com.friendzrandroid.R.raw.tutorial5

        binding.gifImageView.setVideoURI(Uri.parse(filePlace))
        binding.gifImageView.requestFocus()


        val surfaceholder: SurfaceHolder = binding.gifImageView.getHolder()
//        surfaceholder.setFormat(PixelFormat.TRANSPARENT)

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