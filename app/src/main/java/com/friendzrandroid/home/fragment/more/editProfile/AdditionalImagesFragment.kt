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
import androidx.navigation.fragment.navArgs
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.SelectImageUtil
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.core.utils.show
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

    val selectedImages: ArrayList<File> = arrayListOf()

    lateinit var imageUtil: SelectImageUtil

    val args by navArgs<AdditionalImagesFragmentArgs>()
    val images by lazy {
        args.additionalImages
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


//        images.forEach {
//            selectedImages.add(File(it))
//        }

        imageNumber = images.size

        val obs = MutableLiveData<Uri>()
        imageUtil = SelectImageUtil(this, obs)
        obs.observe(viewLifecycleOwner) {
            file = File(it.path)
            firstImageFile = it.path

            selectedImages.add(file!!)

            when (imageNumber) {
                1 -> {
                    binding.imgAdditionalImage.loadImage(file!!.path)
                    binding.additionalImagesClose.show()
                }
                2 -> {
                    binding.imgAdditionalImage1.loadImage(file!!.path)
                    binding.additionalImagesClose2.show()
                }
                3 -> {
                    binding.imgAdditionalImage2.loadImage(file!!.path)
                    binding.additionalImagesClose3.show()
                }
                4 -> {
                    binding.imgAdditionalImage3.loadImage(file!!.path)
                    binding.additionalImagesClose4.show()
                }
                5 -> {
                    binding.imgAdditionalImage4.loadImage(file!!.path)
                    binding.additionalImagesClose5.show()
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

            viewModel.updateAdditionalImages(selectedImages)
            viewModel.isAdditionalImagesUploaded.observe(viewLifecycleOwner) {
                if (it)
                    findNavController().popBackStack()
            }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (images.isNotEmpty())
            setupImages()
    }

    private fun setupImages() {
        images.forEachIndexed { index, s ->
            when (index) {
                0 -> {
                    binding.imgAdditionalImage.loadImage(s)
                    binding.additionalImagesClose.show()
                }

                1 -> {
                    binding.imgAdditionalImage1.loadImage(s)
                    binding.additionalImagesClose2.show()
                }

                2 -> {
                    binding.imgAdditionalImage2.loadImage(s)
                    binding.additionalImagesClose3.show()
                }

                3 -> {
                    binding.imgAdditionalImage3.loadImage(s)
                    binding.additionalImagesClose4.show()
                }

                4 -> {
                    binding.imgAdditionalImage4.loadImage(s)
                    binding.additionalImagesClose5.show()
                }


            }
        }
    }

}