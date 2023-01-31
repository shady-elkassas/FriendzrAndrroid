package com.friendzrandroid.home.fragment.home.feed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.paggingList.PagingViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.model.FeedRequestUserData
import com.friendzrandroid.home.domain.interactor.requests.ChangeRequestStatusUseCase
import com.friendzrandroid.home.domain.interactor.feed.FeedAllUsersUseCase
import com.friendzrandroid.home.domain.interactor.userSettingsAndProfile.UpdateUserSettingsUseCase
import com.friendzrandroid.home.domain.model.ChangeFeedStatusRequest
import com.friendzrandroid.home.domain.model.PagingListRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    val feedUseCase: FeedAllUsersUseCase,
    val updateUserStatusUseCase: ChangeRequestStatusUseCase,
    val updatePrivateModeStatusUseCase: UpdateUserSettingsUseCase,
    //val userSettingsUseCase: UserSettingsUseCase
) : PagingViewModel<FeedRequestUserData>() {
    var degree = 0
    var userLat: Double = 0.0
    var userLong: Double = 0.0
    var sortByInterestMatch: Boolean = false
    val messageError = MutableLiveData<String>()

    val privateModeState = MutableLiveData<Boolean>()
    val reLoadFeed = MutableLiveData<Boolean>()
    //private lateinit var userSettings: UserSettingsResponse

    private val TAG = "FeedViewModel"

    suspend fun updateUserStatus(userid: String, key: Int): Boolean {

        val data =
            validateResponse(updateUserStatusUseCase.execute(ChangeFeedStatusRequest(userid, key)))

        if (data != null)
            return data.isSuccessful
        return false
    }

    override fun loadData() {

        val req =
            PagingListRequest(pageSize, pageNumber, userLat, userLong, degree, sortByInterestMatch)
        Log.e(TAG, "loadData: ${req.degree}")
//        req.degree = degree
//        req.userLat = userLat
//        req.userLong = userLong

        feedUseCase.execute(req).flowOn(Dispatchers.IO)
            .onEach {
                val response = validateResponse(it)
                response?.let {
                    if (it.isSuccessful) {
                        totalItemCount.value = it.model!!.totalRecords

                        setData(it.model.data)

                    } else {
                        messageError.postValue(it.message)
                        if (pageNumber == 1) {
                            totalItemCount.value = 0
                        }
                    }

                }
            }.launchIn(viewModelScope)
    }

    init {
        getSettings()
    }

    fun getSettings() {
        val ghostModeSettings = UserSessionManagement.getGhostMode()
        Log.i(TAG, "getSettings: Mode: $ghostModeSettings")
        privateModeState.value = ghostModeSettings

//        userSettingsUseCase.execute(Any()).flowOn(Dispatchers.IO)
//            .onEach {
//
//                val response = validateResponse(it)
//
//                if (response != null && response.isSuccessful) {
//                    privateModeState.value = response.model?.allowMyAppearanceType != 0
//
//                    userSettings = response.model!!
//                    Log.w(TAG, "getSettings: $userSettings")
//                }
//
//            }.launchIn(viewModelScope)
    }

    fun updateUserPrivateMode(hideFrom: List<Int>, ghostMode: Boolean) {


        //Log.w(TAG, "updateUserPrivateMode: $userSettings")
        Log.w(TAG, "updateUserPrivateMode: HideFrom = $hideFrom")


        val userSettings = UserSessionManagement.getUserSettings()
            .copy(myAppearanceTypes = hideFrom.toString(), ghostmode = ghostMode)

        updatePrivateModeStatusUseCase.execute(userSettings).flowOn(Dispatchers.IO).onEach {


            val response = validateResponse(it)
            Log.i(TAG, "updateUserPrivateMode: Response Result ${response?.model}")
            if (response?.isSuccessful == true) {
                UserSessionManagement.saveUserSettings(response.model!!)
                privateModeState.value = response.model.ghostmode
                val ghostmode = response.model.ghostmode
                reLoadFeed.value = ghostMode

//                pageNumber=1
//                loadData()


            }

        }.launchIn(viewModelScope)
    }
}