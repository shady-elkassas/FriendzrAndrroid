package com.friendzrandroid.home.fragment.more.editProfile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseFragment
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.*
import com.friendzrandroid.databinding.FragmentAdditionalImagesBinding
import com.friendzrandroid.home.data.model.enum.NeedToUpdateStatus
import com.friendzrandroid.utils.FileUtliKotlin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayOutputStream
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

    //    val args: AdditionalImagesFragmentArgs by navArgs()
    val images: Array<String>? by lazy {
        arguments?.getStringArray("additionalImages")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        images?.forEach {

            Glide.with(requireContext())
                .asBitmap()
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : CustomTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        //Save image file
                        val imageUri = getImageUri(resource)

                        if (imageUri != null) {
                            val path = FileUtliKotlin().getPath(requireContext(), imageUri)
                            selectedImages.add(File(path))

                        }
                    }

                })


        }

        imageNumber = images?.size ?: 0

        val obs = MutableLiveData<Uri>()
        imageUtil = SelectImageUtil(this, obs)
        obs.observe(viewLifecycleOwner) {
            file = File(it.path)
            firstImageFile = it.path

            if (imageNumber > selectedImages.size)
                selectedImages.add(file!!)
            else
                selectedImages.add(imageNumber, file!!)

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


    fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                inImage,
                "Title",
                null
            )
        return Uri.parse(path)
    }

    private fun setClicks() {

        binding.btnBack.setOnClickListener {

            findNavController().popBackStack()

        }

        binding.btnAddAdditionalImagesSave.setOnClickListener {

            Log.e("images", "setClicks: ${selectedImages.size}")

            viewModel.updateAdditionalImages(selectedImages)
            viewModel.isAdditionalImagesUploaded.observe(viewLifecycleOwner) {
                if (it && UserSessionManagement.userNeedToUpdate() == NeedToUpdateStatus.UPDATE_PROFILE.status)
                    requireActivity().supportFragmentManager.popBackStack()
                else
                    findNavController().popBackStack()
            }

        }

        binding.imgAdditionalImage.setOnClickListener {
            imageNumber = 1
            imageUtil.selectImage(false)
        }
        binding.additionalImagesClose.setOnClickListener {
            binding.imgAdditionalImage.setImageResource(R.drawable.place_holder)
            selectedImages.removeAt(0)
            it.hide()
        }


        binding.imgAdditionalImage1.setOnClickListener {
            imageNumber = 2
            imageUtil.selectImage(false)
        }
        binding.additionalImagesClose2.setOnClickListener {
            binding.imgAdditionalImage1.setImageResource(R.drawable.place_holder)

            if (selectedImages.size < 2)
                selectedImages.removeLast()
            else
                selectedImages.removeAt(1)
            it.hide()
        }


        binding.imgAdditionalImage2.setOnClickListener {
            imageNumber = 3
            imageUtil.selectImage(false)
        }
        binding.additionalImagesClose3.setOnClickListener {
            binding.imgAdditionalImage2.setImageResource(R.drawable.place_holder)

            if (selectedImages.size < 3)
                selectedImages.removeLast()
            else
                selectedImages.removeAt(2)
            it.hide()
        }

        binding.imgAdditionalImage3.setOnClickListener {
            imageNumber = 4
            imageUtil.selectImage(false)
        }
        binding.additionalImagesClose4.setOnClickListener {
            binding.imgAdditionalImage3.setImageResource(R.drawable.place_holder)

            if (selectedImages.size < 4)
                selectedImages.removeLast()
            else
                selectedImages.removeAt(3)

            it.hide()
        }


        binding.imgAdditionalImage4.setOnClickListener {
            imageNumber = 5
            imageUtil.selectImage(false)
        }
        binding.additionalImagesClose5.setOnClickListener {
            binding.imgAdditionalImage4.setImageResource(R.drawable.place_holder)

            if (selectedImages.size < 5)
                selectedImages.removeLast()
            else
                selectedImages.removeAt(4)

            it.hide()
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (images?.isNotEmpty() == true)
            setupImages()
    }

    private fun setupImages() {
        images?.forEachIndexed { index, s ->
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