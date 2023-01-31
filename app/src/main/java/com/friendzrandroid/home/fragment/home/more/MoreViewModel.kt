package com.friendzrandroid.home.fragment.home.more

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.home.domain.interactor.LinkClickUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val linkClickUseCase: LinkClickUseCase
) : BaseViewModel() {
    val messageError = MutableLiveData<String>()
    val moreSuccessData = MutableLiveData<String>()


    fun sendLinkClick(clickedScreen: String) {

        linkClickUseCase.execute(clickedScreen).flowOn(Dispatchers.IO).onEach {

            val response = validateResponse(it)

            response?.let {
                if (it.isSuccessful) {
                    moreSuccessData.postValue(it.model!!)

                    val model = it.model
                    Log.e("testCount", " the model id $model,clickedScreen is : $clickedScreen ")

                } else {
                    messageError.postValue(it.message)
                    val message = it.message
                    Log.e("testCount", " the model id $message,clickedScreen is : $clickedScreen ")


                }

            }

        }.launchIn(viewModelScope)

    }


}