package com.friendzrandroid.splash.presentation.fragment

import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.FragmentIntro1PeopleBinding
import com.friendzrandroid.databinding.FragmentIntro1PeopleBindingImpl
import com.friendzrandroid.databinding.FragmentIntro2Binding
import com.friendzrandroid.databinding.FragmentUserReportBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Intro2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Intro2Fragment : Fragment() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentIntro2Binding.inflate(layoutInflater)
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

//
//        binding.btnIntroNext.text = getString(R.string.getStarted)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            binding.btnIntroExit.text= Html.fromHtml("<font color='#0BBEA1'>In a hurry? Skip to <u>profile</u></font>", Html.FROM_HTML_MODE_COMPACT)
//        } else {
//            binding.btnIntroExit.text= (Html.fromHtml("<font color='#0BBEA1'>In a hurry? Skip to <u>profile</u></font>"));
//        }
//        binding.btnIntroExit.show()


        val filePlace =
            "android.resource://" + context!!.packageName + "/raw/" + com.friendzrandroid.R.raw.tutorial2

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