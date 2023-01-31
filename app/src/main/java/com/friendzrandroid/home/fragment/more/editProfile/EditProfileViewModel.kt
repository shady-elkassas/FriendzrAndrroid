package com.friendzrandroid.home.fragment.more.editProfile

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.rekognition.model.CompareFacesResult
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.data.repository.FaceComparingRepository
import com.friendzrandroid.home.domain.interactor.tags.GetIamUseCase
import com.friendzrandroid.home.domain.interactor.tags.GetInterestsUseCase
import com.friendzrandroid.home.domain.interactor.tags.GetPreferUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.GetConfigurationValidationUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.LogoutAccountUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.MyProfileUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.UpdateMyProfileUseCase
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val myProfileUseCase: MyProfileUseCase,
    private val updateProfileUseCase: UpdateMyProfileUseCase,
    private val interestsUseCase: GetInterestsUseCase,
    private val iamUseCase: GetIamUseCase,
    private val preferUseCase: GetPreferUseCase,
    private val logOutUseCase: LogoutAccountUseCase,
    private val faceComparingRepository: FaceComparingRepository,
    private val configurationValidationUseCase: GetConfigurationValidationUseCase
) : BaseViewModel() {

    val isLoading = MutableLiveData<Boolean>()

    val allInterestList = ArrayList<InterestData>()
    val allIamList = ArrayList<InterestData>()
    val allIamHashList = ArrayList<TagsModel>()
    val allPreferList = ArrayList<InterestData>()
    val allPreferHashList = ArrayList<TagsModel>()

    val myProfileData = MutableLiveData<UserProfileData>()

    val userDataUpdated = MutableLiveData<DataState<UserProfileData>>()

    val isLoggedOut = MutableLiveData<Boolean>()

    var userYear: Int = 0
    var userMonth: Int = 0
    var userDay: Int = 0

    val awsData: MutableLiveData<DataState<ConfigurationValidation>> = MutableLiveData()
    val faceComparingResult: MutableLiveData<CompareFacesResult> = MutableLiveData()

    fun setUserDate(date: String) {
        if (!date.isNullOrEmpty()) {

            val da = date.split('-')
            userYear = da[2].toInt()
            userMonth = da[1].toInt() - 1
            userDay = da[0].toInt()

        }
    }


    fun getUserInterests() {
        isLoading.postValue(true)
        interestsUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response!!.isSuccessful) {

                    allInterestList.clear()

                    allInterestList.addAll(response.model!!)


                }
            } else {


            }

            isLoading.postValue(false)

        }.launchIn(viewModelScope)
    }


    fun getUserIam() {
        isLoading.postValue(true)

        iamUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response!!.isSuccessful) {

                    allIamList.clear()

                    allIamList.addAll(response.model!!)


                    for (data in allIamList) {
                        allIamHashList.add(TagsModel(data.id, data.name))
                    }

                    UserSessionManagement.saveUserIamHash(allIamHashList)

                }
            } else {


            }

            isLoading.postValue(false)

        }.launchIn(viewModelScope)
    }

    fun getUserPrefer() {
        isLoading.postValue(true)
        preferUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response!!.isSuccessful) {

                    allPreferList.clear()

                    allPreferList.addAll(response.model!!)



                    for (data in allPreferList) {
                        allPreferHashList.add(TagsModel(data.id, data.name))
                    }
                    UserSessionManagement.saveUserIPrefereHash(allPreferHashList)


                }
            } else {


            }

            isLoading.postValue(false)

        }.launchIn(viewModelScope)
    }

    fun updateData(
        userName: String,
        universityCode: String,
        dateOfBirth: String,
        gender: String,
        genderOther: String?,
        bio: String,
        tagsInterest: String,
        tagsIam: String,
        tagsPrefer: String,
        userImage: File?
    ) {

        isLoading.postValue(true)


        val requestBodyMap = HashMap<String, RequestBody>()

        requestBodyMap.put("Username", RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            userName
        ))


        requestBodyMap.put("universityCode", RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            universityCode
        ))

        requestBodyMap.put(
            "birthdate",
            RequestBody.create("text/plain".toMediaTypeOrNull(), dateOfBirth)
        )
        requestBodyMap.put(
            "Email",
            RequestBody.create("text/plain".toMediaTypeOrNull(), myProfileData.value!!.email)
        )
        requestBodyMap.put("bio", RequestBody.create("text/plain".toMediaTypeOrNull(), bio))
        requestBodyMap.put("Gender", RequestBody.create("text/plain".toMediaTypeOrNull(), gender))
        requestBodyMap.put(
            "otherGenderName",
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                genderOther ?: ""
            )
        )
        requestBodyMap.put(
            "listoftags",
            RequestBody.create("text/plain".toMediaTypeOrNull(), tagsInterest)
        )
        requestBodyMap.put("Iam", RequestBody.create("text/plain".toMediaTypeOrNull(), tagsIam))
        requestBodyMap.put(
            "preferto",
            RequestBody.create("text/plain".toMediaTypeOrNull(), tagsPrefer)
        )


        var filePart: MultipartBody.Part? = null
        if (userImage != null) {
            filePart = MultipartBody.Part.createFormData(
                "UserImags",
                 URLEncoder.encode(userImage.getName(), "utf-8"),
                RequestBody.create("image/*".toMediaTypeOrNull(), userImage)
            )
        }

        updateProfileUseCase.execute(FormDataRequestWithImage(requestBodyMap, filePart))
            .flowOn(Dispatchers.IO).onEach {
                isLoading.postValue(false)
                val response = validateResponse(it)

                response?.let {
                    if (it.isSuccessful && it.model != null) {
                        UserSessionManagement.saveUserData(response.model!!)
                        userDataUpdated.postValue(DataState.NewData(it.model))
                    } else

                        userDataUpdated.postValue(DataState.FailData(it.message))


                }


//                if (response != null && response.isSuccessful) {
//                    if (response.model!!.needUpdate == 0) {
//                        UserSessionManagement.saveUserData(response.model)
//                        userDataUpdated.postValue(true)
//                    }
//                }


            }.launchIn(viewModelScope)

    }




    fun getMyProfile() = myProfileData.postValue(UserSessionManagement.getUserData())

    fun logOut() = viewModelScope.launch {

        isLoading.postValue(true)

        val response = validateResponse(logOutUseCase.execute())
        response?.let {

            isLoading.postValue(false)

            if (it.isSuccessful) {

                FirebaseMessaging.getInstance().deleteToken()
                UserSessionManagement.removeUserSession()

                delay(100)
                isLoggedOut.postValue(true)

            }
        }
    }


    fun getAwsCredentials() {
        configurationValidationUseCase.execute(Any())
            .flowOn(Dispatchers.IO)
            .onEach {
                val result = validateResponse(it)
                result?.let {
                    if (it.isSuccessful)
                        awsData.postValue(DataState.NewData(it.model!!))
                    else
                        awsData.postValue(DataState.FailData(it.message))
                }
            }.launchIn(viewModelScope)
    }

    fun comparingFace(
        activity: Activity,
        accessKey: String,
        secretKey: String,
        firstImage: String,
        secondImage: String
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            val result =
                faceComparingRepository.getComparingResult(
                    activity,
                    accessKey,
                    secretKey,
                    firstImage,
                    secondImage
                )

            result?.let {
                faceComparingResult.postValue(it)
            }
        }

//    fun getMyProfile() {
//
//        myProfileUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {
//
//            val response = validateResponse(it)
//            if (response != null) {
//                if (response.isSuccessful)
//                    myProfileData.postValue(response.model!!)
//                else {
////                    myProfileData.postValue(DataState.FailData(response.message))
//                }
//
//            } else {
////                myProfileData.postValue(DataState.FailData("something went wrong try again later"))
//            }
//
//        }.launchIn(viewModelScope)
//    }

}