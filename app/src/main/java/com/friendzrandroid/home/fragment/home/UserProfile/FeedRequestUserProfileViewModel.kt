package com.friendzrandroid.home.fragment.home.UserProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.UserProfileData
import com.friendzrandroid.home.domain.interactor.requests.ChangeRequestStatusUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.UserProfileUseCase
import com.friendzrandroid.home.domain.model.ChangeFeedStatusRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedRequestUserProfileViewModel @Inject constructor(
    private val userProfileUserCase: UserProfileUseCase,
    private val changeStateUseCase: ChangeRequestStatusUseCase
) : BaseViewModel() {


    val userProfile =
        MutableLiveData<DataState<UserProfileData>>(DataState.Loading(null))
    val updateUserState =
        MutableLiveData<DataState<Boolean>>()


    fun getUserProfile(userID: String) {

        userProfile.postValue(DataState.Loading(null))

        userProfileUserCase.execute(userID).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)
            response?.let {
                if (it.isSuccessful) {
                    userProfile.postValue(DataState.NewData(it.model!!))
                } else {
                    userProfile.postValue(DataState.FailData(it.message))

                }
            }

        }.launchIn(viewModelScope)
    }


    fun changeStatus(userID: String, newState: Int, isNotFriend: Boolean? = null) {

        updateUserState.postValue(DataState.Loading(null))
        viewModelScope.launch {

            val response = validateResponse(
                changeStateUseCase.execute(
                    ChangeFeedStatusRequest(
                        userID,
                        newState, isNotFriend
                    )
                )
            )

            response?.let {
                if (it.isSuccessful)
                    updateUserState.postValue(DataState.NewData(it.isSuccessful))
                else
                    updateUserState.postValue(DataState.FailData(it.message))
            }
        }
    }

}