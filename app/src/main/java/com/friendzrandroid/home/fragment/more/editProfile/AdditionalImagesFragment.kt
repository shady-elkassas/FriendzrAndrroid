package com.friendzrandroid.home.fragment.more.editProfile

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.SelectImageUtil
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.databinding.FragmentAdditionalImagesBinding

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AdditionalImagesFragment : BaseFragment() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentAdditionalImagesBinding.inflate(layoutInflater)
    }
    private val viewModel: EditProfileViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel
    var file: File? = null
    var firstImageFile: String? = null
    var imageNumber: Int = 0

    lateinit var imageUtil: SelectImageUtil
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val obs = MutableLiveData<Uri>()
        imageUtil = SelectImageUtil(this, obs)
        obs.observe(viewLifecycleOwner) {
            file = File(it.path)
            firstImageFile = it.path



            when (imageNumber) {
                1 -> {
                    binding.imgAdditionalImage.loadImage(file!!.path)

                }
                2 -> {
                    binding.imgAdditionalImage1.loadImage(file!!.path)

                }
                3 -> {
                    binding.imgAdditionalImage2.loadImage(file!!.path)

                }
                4 -> {
                    binding.imgAdditionalImage3.loadImage(file!!.path)

                }
                5 -> {
                    binding.imgAdditionalImage4.loadImage(file!!.path)

                }


            }
        }

        setObserve()
        setClicks()

        return binding.root
    }

    private fun setObserve() {


    }

    private fun setClicks() {

        binding.btnBack.setOnClickListener {

            findNavController().popBackStack()

        }

        binding.btnAddAdditionalImagesSave.setOnClickListener {

            findNavController().popBackStack()

        }
        binding.con.setOnClickListener {
            imageNumber = 1

            imageUtil.selectImage(false)


        }

        binding.con1.setOnClickListener {
            imageNumber = 2

            imageUtil.selectImage(false)


        }
        binding.con2.setOnClickListener {
            imageNumber = 3

            imageUtil.selectImage(false)

        }
        binding.con3.setOnClickListener {
            imageNumber = 4

            imageUtil.selectImage(false)


        }
        binding.con4.setOnClickListener {
            imageNumber = 5
            imageUtil.selectImage(false)


        }


    }

}