package com.friendzrandroid.home.fragment.more.myProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.MyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val myProfileUseCase: MyProfileUseCase
) : BaseViewModel() {

    val myProfileData = MutableLiveData<DataState<UserProfileData>>()

    fun getMyProfile() {
        myProfileData.postValue(DataState.Loading(null))

        myProfileUseCase.execute(Any()).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            if (response != null) {
                if (response.isSuccessful)
                    myProfileData.postValue(DataState.NewData(response.model!!))
                else {
                    myProfileData.postValue(DataState.FailData(response.message))
                }

            } else {
                myProfileData.postValue(DataState.FailData("something went wrong try again later"))
            }

        }.launchIn(viewModelScope)
    }

}