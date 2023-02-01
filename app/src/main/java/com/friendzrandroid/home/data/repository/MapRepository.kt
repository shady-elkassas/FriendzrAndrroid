package com.friendzrandroid.home.data.repository

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.datasource.EventAPIS
import com.friendzrandroid.home.data.datasource.MapAPIS
import com.friendzrandroid.home.data.model.EventByLocationResponse
import com.friendzrandroid.home.data.model.EventAllLocationResponse
import com.friendzrandroid.home.data.model.EventAroundMeResponse
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class MapRepository @Inject constructor(private val mapApi: MapAPIS) : SafeApiRequest() {


    suspend fun getAllLocationEvent(): ResultWrapper<BaseDataWrapper<EventAllLocationResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest { mapApi.getAllLocationEvent(token!!) } as ResultWrapper<BaseDataWrapper<EventAllLocationResponse>>
    }


    suspend fun getAroundMeEvents(
        params: LatLng,
        filterSelectedTags: String,
        dateCriteria: String?,
        startDate: String?,
        endDate: String?,
    ): ResultWrapper<BaseDataWrapper<EventAroundMeResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            if (token != null && !token.equals("")) {

                mapApi.getEventsAroundMe(
                    token,
                    filterSelectedTags,
                    params.longitude,
                    params.latitude,
                    dateCriteria, startDate, endDate
                )

            } else {

                mapApi.getEventsAroundMePublic(params.longitude, params.latitude)

            }

        } as ResultWrapper<BaseDataWrapper<EventAroundMeResponse>>
    }


    suspend fun getEventsByLocation(latLng: LatLng): ResultWrapper<BaseDataWrapper<EventByLocationResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {
            mapApi.getEventsByLocation(
                token!!,
                latLng.latitude,
                latLng.longitude
            )
        } as ResultWrapper<BaseDataWrapper<EventByLocationResponse>>

    }

}