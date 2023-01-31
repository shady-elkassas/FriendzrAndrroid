package com.friendzrandroid.home.data.repository

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import com.friendzrandroid.core.utils.FormatToDate
import com.friendzrandroid.core.utils.FormatToTime
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.datasource.EventAPIS
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.friendzrandroid.home.domain.model.PagingListRequest
import java.util.*
import javax.inject.Inject

class EventRepository @Inject constructor(private val api: EventAPIS) : SafeApiRequest() {


    suspend fun getInterests(): ResultWrapper<BaseDataWrapper<List<InterestData>>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest { api.getAllInterests(token!!) } as ResultWrapper<BaseDataWrapper<List<InterestData>>>
    }

    suspend fun getCategories(): ResultWrapper<BaseDataWrapper<List<InterestData>>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {

            if (token != null && !token.equals("")) {
                api.getAllCategories(token!!)

            } else {
                api.getAllCategoriesPublic()

            }


        } as ResultWrapper<BaseDataWrapper<List<InterestData>>>
    }

    suspend fun getEvents(data: PagingListRequest): ResultWrapper<BaseDataWrapper<EventsResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getEvents(
                token!!,
                data.pageSize,
                data.PageNumber,
                data.search
            )
        } as ResultWrapper<BaseDataWrapper<EventsResponse>>
    }

    suspend fun getOnlyEventsAround(data: PagingListRequest): ResultWrapper<BaseDataWrapper<EventsResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            if (token != null && !token.equals("")) {
                api.getOnlyEventsAround(
                    token,
                    data.pageSize,
                    data.PageNumber,
                    data.userLat,
                    data.userLang,
                    data.filterSelectedTags
                )
            } else {
                api.getOnlyEventsAroundPublic(
                    data.pageSize,
                    data.PageNumber,
                    data.userLat,
                    data.userLang
                )
            }

        } as ResultWrapper<BaseDataWrapper<EventsResponse>>
    }

    suspend fun getEventAttendance(eventID: String): ResultWrapper<BaseDataWrapper<EventAttende>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getEventAttendace(
                token!!,
                eventID
            )
        } as ResultWrapper<BaseDataWrapper<EventAttende>>
    }


    suspend fun getEventsDetails(id: String): ResultWrapper<BaseDataWrapper<EventDetails>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getEventDetails(
                token!!,
                id
            )
        } as ResultWrapper<BaseDataWrapper<EventDetails>>
    }

    suspend fun getEventTypes(): ResultWrapper<BaseDataWrapper<List<EventTypesResponse>>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {
            api.getEventType(
                token!!
            )
        } as ResultWrapper<BaseDataWrapper<List<EventTypesResponse>>>
    }

    suspend fun AddEvent(data: FormDataRequestWithImage): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.addEvent(
                token!!,
                data.userData,
                data.UserImags
            )
        } as ResultWrapper<BaseDataWrapper<Any>>


    }


    suspend fun EditEvent(data: FormDataRequestWithImage): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.UpdateEvent(
                token!!,
                data.userData,
                data.UserImags
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun joinEvent(data: String): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        val date = Date().FormatToDate()
        val time = Date().FormatToTime()

        return apiRequest {
            api.joinEvent(
                token!!,
                data,
                date,
                time
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun leaveEvent(
        data: String,
        leaveDate: String,
        leaveTime: String
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.leaveEvent(
                token!!,
                data,
                leaveDate,
                leaveTime
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun deleteEvent(data: String): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.deleteEvent(
                token!!,
                data
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun CickOutUserFromEvent(
        eventID: String,
        userID: String,
        status: Int
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        val date = Date().FormatToDate()
        val time = Date().FormatToTime()
        return apiRequest {
            api.cickOutUser(
                token!!,
                eventID, userID, status,
                date,
                time
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }


    suspend fun leaveEventChat(
        eventID: String,
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        val date = Date().FormatToDate()
        val time = Date().FormatToTime()
        return apiRequest {
            api.leaveEventChat(
                token!!,
                eventID,
                date,
                time
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }
}