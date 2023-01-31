package com.friendzrandroid.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.friendzrandroid.auth.data.model.LoginResponseData
import com.friendzrandroid.home.data.model.*
import com.friendzrandroid.home.domain.model.UserSettingsRequest
import com.google.firebase.messaging.Constants.MessageNotificationKeys.NOTIFICATION_COUNT
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UserSessionManagement {

    private val GHOST_MOOD = "ghostmode"
    private val ALLOW_PUSH_NOTIFICATIONS = "pushnotification"
    private val IS_WHITE_LABEL = "isWhiteLabel"
    private val ALLOW_MYLOCATION = "allowMyLocation"
    private val USER_NAME = "UserName"
    private val UNIVERSITY_CODE = "universityCode"
    private val USER_NEED_TO_UPDATE = "UserIsNeedToUpdate"

    private val USER_IMAGE = "UserImage"
    private val USER_TOKEN = "UserToken"
    private val FROM_SETTINGS = "fromSettings"
    private val MY_APPEARANCE_TYPE = "allowMyAppearanceType"
    private val MY_INTERESTS_FOR_PROFILE_COMPAIRING = "myInterestsForProfile"
    private val USER_LOGED_IN = "UserIsLogged"

    private val USER_GENDER = "UserGender"
    private val USER_OTHER_GENDER = "UserOtherGender"
    private val USER_ENJOY_TAGS = "UserEnjoyTags"
    private val USER_IAM_TAGS = "UserIamTags"
    private val USER_IAM_HASHTAG = "UserIamHashTag"
    private val USER_IPREFERE_HASHTAG = "UserIPrefereHashTag"
    private val USER_PREFER_TAGS = "UserPreferTags"
    private val USER_MAP_FILTER_TAGS = "UserMapFilterTags"
    private val USER_MAP_FILTER_STATUS = "UserMapFilterStatus"
    private val USER_BIO = "UserBio"
    private val USER_AGE = "UserAge"
    private val USER_BIRTH_DAY = "UserBirthDay"
    private val USER_DISPLAY_NAME = "UserDisplayName"
    private val USER_EMAIL = "UserEmail"
    private val USER_LANG = "UserLang"
    private val USER_LAT = "UserLat"
    private val USER_KEY = "UserKey"

    private val USER_AGE_FROM = "UserAgeFrom"
    private val USER_AGE_TO = "UserAgeTo"
    private val USER_FILTER_ACCORDINGING_TO_AGE = "UserFilterAccordingToAge"
    private val USER_MANAUAL_DISTANCE_CONTROL = "UserManualDistanceControl"
    private val USER_ALLOW_PERSONAL_SPACE = "UserAllowPersonalSpace"
    private val USER_DISTANCE_FILTER = "UserAllowDistanceFilter"

    private val CONFIG_AGE_FROM = "ConfigurationAgeFrom"
    private val CONFIG_AGE_TO = "ConfigurationAgeTo"
    private val CONFIG_DISTANCE_FROM = "ConfigurationDistanceFrom"
    private val CONFIG_DISTANCE_TO = "ConfigurationDistanceTo"

    private val FCM_TOKEN = "FirebaseToken"

    private val INBOX_COUNT = "InboxCount"
    private val FRIEND_REQUEST_NUMBER = "frindRequestNumber"
    private val NOTIFICATION_COUNT = "NotificationCount"

    private val COMMUNITY_CONNECTIONS_ADS_COUNT = "communityConnectionsAdsCount"
    private val COMMUNITY_EVENTS_ADS_COUNT = "communityEventsAdsCount"


    private val FIRST_OPEN_FEED = "FirstOpenFeed"
    private val FIRST_OPEN_FEED_WITH_TOKEN = "FirstOpenFeedWithToken"
    private val FIRST_OPEN_MAP = "FirstOpenMap"
    private val FIRST_OPEN_MAP_WITH_TOKEN = "FirstOpenMapWithToken"

    const val PREF_NAME = "com.friendzrandroid.PREFERENCE_FILE_KEY"


    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    fun init(context: Context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    fun saveUserToken(token: String) {
        //editor.putBoolean(FIRST_OPEN, false)
        editor.putString(USER_TOKEN, token)
        editor.putBoolean(USER_LOGED_IN, true)
        editor.apply()
    }

    fun getFirstOpenFeed() = pref.getBoolean(FIRST_OPEN_FEED, true)

    fun getFirstOpenFeedWithToken() = pref.getBoolean(FIRST_OPEN_FEED_WITH_TOKEN, true)

    fun changeFirstOpenFeed() {
        editor.putBoolean(FIRST_OPEN_FEED, false)
        editor.apply()
    }

    fun changeFirstOpenFeedWithToken() {
        editor.putBoolean(FIRST_OPEN_FEED_WITH_TOKEN, false)
        editor.apply()
    }

    fun getFirstOpenMap() = pref.getBoolean(FIRST_OPEN_MAP, true)
    fun getFirstOpenMapWithToken() = pref.getBoolean(FIRST_OPEN_MAP_WITH_TOKEN, true)

    fun changeFirstOpenMap() {
        editor.putBoolean(FIRST_OPEN_MAP, false)
        editor.apply()
    }

    fun changeFirstOpenMapWithToken() {
        editor.putBoolean(FIRST_OPEN_MAP_WITH_TOKEN, false)
        editor.apply()
    }


    fun getRequestsNumber() = pref.getInt(FRIEND_REQUEST_NUMBER, 0)
    fun updateFriendRequestNumber(number: Int) {
        editor.putInt(FRIEND_REQUEST_NUMBER, number)
        editor.apply()
    }


    fun getInboxNumber() = pref.getInt(INBOX_COUNT, 0)
    fun updateInboxNumber(number: Int) {
        editor.putInt(INBOX_COUNT, number)
        editor.apply()
    }


    fun getNotificationNumber() = pref.getInt(NOTIFICATION_COUNT, 0)
    fun updateNotificationNumber(number: Int) {
        editor.putInt(NOTIFICATION_COUNT, number)
        editor.apply()
    }

    fun saveUserFirebaseToken(token: String) {
        editor.putString(FCM_TOKEN, token)
        editor.apply()
    }

    fun getUserFirebaseToken() = pref.getString(FCM_TOKEN, "")

    fun getGhostMode(): Boolean {
        return pref.getBoolean(GHOST_MOOD, false)
    }

    fun saveGhostMode(ghostmode: Boolean) {
        editor.putBoolean(GHOST_MOOD, ghostmode)

        editor.apply()
    }

    fun getMyAppearanceType(): List<Int> {

        var value: List<Int> = listOf(0)

        val gson = Gson()
        val json = pref.getString(MY_APPEARANCE_TYPE, "[]")
        val type = object : TypeToken<List<Int>>() {}.type//converting the json to list
        try {
            value = gson.fromJson(json, type)


        } catch (e: java.lang.NullPointerException) {

        }


        return value
        //returning the list
    }



    fun getInterestsForProfileType(): List<String> {

        val gson = Gson()
        val json = pref.getString(MY_INTERESTS_FOR_PROFILE_COMPAIRING, "[]")
        val type = object : TypeToken<List<String>>() {}.type//converting the json to list
        return gson.fromJson(json, type)//returning the list


    }

    fun getTagsList(tagsString: String): List<TagsModel> {
        val gson = Gson()
        val json = pref.getString(tagsString, "[]")
        val type = object : TypeToken<List<TagsModel>>() {}.type//converting the json to list
        return gson.fromJson(json, type) //returning the list

    }


    fun getTagsObject(tagsString: String): TagsModel {

        var tagsModel = TagsModel("d5564159-627e-4fea-a602-1e953b9d0c0f", "#")

        if (tagsString.equals("UserIPrefereHashTag")) {

            tagsModel = TagsModel("d5564159-627e-4fea-a602-1e953b9d0c0f", "#")
        } else if (tagsString.equals("UserIamHashTag")) {
//            defultValue = "ac22a6ab-94ad-4813-a14a-65e1af300cbe"

            tagsModel = TagsModel("ac22a6ab-94ad-4813-a14a-65e1af300cbe", "#")

        }

        val gson = Gson()

        val json = pref.getString(tagsString, "{}")
        val obj: TagsModel = gson.fromJson(json, TagsModel::class.java)

        if (json.equals("{}")) {
            return tagsModel
        } else {
            return obj //returning the object
        }
    }


    fun convertListToJson(data: List<TagsModel>) = Gson().toJson(data)

    fun convertObjectToJson(data: TagsModel): String {
        val gson = Gson()
        return gson.toJson(data)

    }

    fun getIamHashTag(): TagsModel {


        return getTagsObject(USER_IAM_HASHTAG)
    }

    fun getIPrefereHashTag(): TagsModel {

        return getTagsObject(USER_IPREFERE_HASHTAG)
    }

    fun saveUserData(data: LoginResponseData) {
        //val gson = Gson()
        //val json = gson.toJson(data.allowMyAppearanceType)
        editor.putString(MY_APPEARANCE_TYPE, Gson().toJson(data.myAppearanceTypes))

        editor.putString(MY_INTERESTS_FOR_PROFILE_COMPAIRING, Gson().toJson(data.interests))

        editor.putString(USER_TOKEN, data.token)
        editor.putBoolean(USER_LOGED_IN, true)
        editor.putString(USER_NAME, data.userName)
        editor.putString(USER_IMAGE, data.userImage)
        editor.putBoolean(IS_WHITE_LABEL, data.isWhiteLable)
        editor.putBoolean(ALLOW_MYLOCATION, data.allowmylocation)
        editor.putBoolean(ALLOW_PUSH_NOTIFICATIONS, data.pushnotification)
        editor.putBoolean(GHOST_MOOD, data.ghostmode)
        editor.putInt(USER_NEED_TO_UPDATE, data.needUpdate)
        editor.putString(USER_EMAIL, data.email)

        editor.putInt(FRIEND_REQUEST_NUMBER, data.frindRequestNumber)
        editor.putInt(INBOX_COUNT, data.message_Count)
        editor.putInt(NOTIFICATION_COUNT, data.notificationcount)



        editor.apply()


    }

    fun getUserSettings() =
        UserSettingsRequest(
            allowmylocation = pref.getBoolean(ALLOW_PUSH_NOTIFICATIONS, false),
            pushnotification = pref.getBoolean(ALLOW_PUSH_NOTIFICATIONS, false),
            ghostmode = getGhostMode(),
            manualdistancecontrol = pref.getLong(USER_MANAUAL_DISTANCE_CONTROL, 0).toDouble(),
            filteringaccordingtoage = pref.getBoolean(USER_FILTER_ACCORDINGING_TO_AGE, false),
            ageto = pref.getInt(USER_AGE_TO, 0),
            agefrom = pref.getInt(USER_AGE_FROM, 0),
            personalSpace = pref.getBoolean(USER_ALLOW_PERSONAL_SPACE, false),
            distanceFilter = pref.getBoolean(USER_DISTANCE_FILTER, false),


            myAppearanceTypes = getMyAppearanceType().toString()


        )


    fun saveUserSettings(data: UserSettingsResponse) {

        editor.putInt(USER_AGE_FROM, data.agefrom)
        editor.putInt(USER_AGE_TO, data.ageto)
        editor.putBoolean(ALLOW_MYLOCATION, data.allowmylocation)
        editor.putBoolean(USER_FILTER_ACCORDINGING_TO_AGE, data.filteringaccordingtoage)
        editor.putBoolean(GHOST_MOOD, data.ghostmode)
        editor.putLong(USER_MANAUAL_DISTANCE_CONTROL, data.manualdistancecontrol.toLong())
        editor.putBoolean(ALLOW_PUSH_NOTIFICATIONS, data.pushnotification)
        editor.putBoolean(USER_ALLOW_PERSONAL_SPACE, data.personalSpace)
        editor.putBoolean(USER_DISTANCE_FILTER, data.distanceFilter)
        editor.putString(MY_APPEARANCE_TYPE, Gson().toJson(data.myAppearanceTypes))

        editor.apply()
    }

    fun saveUserIamHash(list: List<TagsModel>) {

        for (name in list) {
            if (name.tagname.equals("#")) {
                editor.putString(USER_IAM_HASHTAG, convertObjectToJson(name))
            }
        }
    }

    fun saveUserIPrefereHash(list: List<TagsModel>) {
        for (name in list) {
            if (name.tagname.equals("#")) {
                editor.putString(USER_IPREFERE_HASHTAG, convertObjectToJson(name))
            }

        }

    }

    fun saveMapFilter(listoftagsmodel: List<TagsModel>) {

        editor.putString(USER_MAP_FILTER_TAGS, convertListToJson(listoftagsmodel))
        editor.putBoolean(USER_MAP_FILTER_STATUS, true)
        editor.apply()


    }



    fun isMapFilterOpen(): Boolean {
        return pref.getBoolean(USER_MAP_FILTER_STATUS, false)
    }

    fun isWhiteLabel(): Boolean {
        return pref.getBoolean(IS_WHITE_LABEL, false)
    }


    fun saveSettingsPrivateModeStatus(status: Boolean) {

        editor.putBoolean(FROM_SETTINGS, status)
        editor.apply()


    }

    fun isFromSettings(): Boolean {
        return pref.getBoolean(FROM_SETTINGS, false)
    }

    fun deleteMapFilter() {
        editor.putString(USER_MAP_FILTER_TAGS, "")
        editor.putBoolean(USER_MAP_FILTER_STATUS, false)
        editor.apply()
    }

    fun saveUserData(data: UserProfileData) {

        var interestsList: ArrayList<String> = ArrayList()



        for (i in data.listoftagsmodel) {
            interestsList.add(i.tagID)
        }

        editor.putString(MY_INTERESTS_FOR_PROFILE_COMPAIRING, Gson().toJson(interestsList))

        Log.i("TAG Data", "saveUserData: Session $data")

        editor.putString(MY_APPEARANCE_TYPE, Gson().toJson(data.myAppearanceTypes))


        editor.putString(USER_ENJOY_TAGS, convertListToJson(data.listoftagsmodel))




        editor.putString(USER_IAM_TAGS, convertListToJson(data.iamList))

        for (name in data.iamList) {
            if (name.tagname.equals("#")) {
                editor.putString(USER_IAM_HASHTAG, convertObjectToJson(name))
            }
        }

        editor.putString(USER_PREFER_TAGS, convertListToJson(data.prefertoList))

        for (name in data.prefertoList) {
            if (name.tagname.equals("#")) {
                editor.putString(USER_IPREFERE_HASHTAG, convertObjectToJson(name))
            }

        }
        editor.putString(USER_NAME, data.userName)
        editor.putString(UNIVERSITY_CODE, data.universityCode)
        editor.putString(USER_IMAGE, data.userImage)
        editor.putInt(USER_NEED_TO_UPDATE, data.needUpdate)
        editor.putString(USER_GENDER, data.gender)
        editor.putString(USER_OTHER_GENDER, data.otherGenderName)
        editor.putInt(USER_AGE, data.age)
        editor.putBoolean(ALLOW_MYLOCATION, data.allowmylocation)
        editor.putBoolean(ALLOW_PUSH_NOTIFICATIONS, data.pushnotification)
        editor.putString(USER_LANG, data.lang)
        editor.putString(USER_LAT, data.lat)
        editor.putString(USER_BIRTH_DAY, data.birthdate)
        editor.putString(USER_BIO, data.bio)
        editor.putString(USER_DISPLAY_NAME, data.displayedUserName)
        editor.putBoolean(GHOST_MOOD, data.ghostmode)

        editor.apply()


    }

    fun getUserData() = UserProfileData(
        userName = pref.getString(USER_NAME, "")!!,
        universityCode = pref.getString(UNIVERSITY_CODE, "")!!,
        userImage = pref.getString(USER_IMAGE, "")!!,
        gender = pref.getString(USER_GENDER, "")!!,
        otherGenderName = pref.getString(USER_OTHER_GENDER, null),
        listoftagsmodel = getTagsList(USER_ENJOY_TAGS),
        iamList = getTagsList(USER_IAM_TAGS),
        prefertoList = getTagsList(USER_PREFER_TAGS),
        bio = pref.getString(USER_BIO, "")!!,
        age = pref.getInt(USER_AGE, 0),
        myAppearanceTypes = getMyAppearanceType(),
        allowmylocation = pref.getBoolean(ALLOW_MYLOCATION, false),
        birthdate = pref.getString(USER_BIRTH_DAY, "")!!,
        displayedUserName = pref.getString(USER_DISPLAY_NAME, "")!!,
        email = pref.getString(USER_EMAIL, "")!!,
        ghostmode = pref.getBoolean(GHOST_MOOD, false),
        pushnotification = pref.getBoolean(ALLOW_PUSH_NOTIFICATIONS, false),
        needUpdate = pref.getInt(USER_NEED_TO_UPDATE, 0),
        lang = pref.getString(USER_LANG, ""),
        lat = pref.getString(USER_LAT, ""),
        key = pref.getInt(USER_KEY, 0)
    )


    fun isUserLoggedIn(): Boolean {
        return pref.getBoolean(USER_LOGED_IN, false)
    }


    fun userNeedToUpdate(): Int {
        val x = pref.getInt(USER_NEED_TO_UPDATE, -1)
        return x
    }

    fun userName(): String {
        val x = pref.getString(USER_NAME, "")
        return x!!
    }

    fun userImage(): String {
        val x = pref.getString(USER_IMAGE, "")
        return x!!
    }

    fun getKeyAuthToken(): String? {

        val token = pref.getString(USER_TOKEN, "")

        return if (token.isNullOrEmpty()) {
            null
        } else {
            "Bearer " + token
        }

    }

    fun saveConfigurationValidation(data: ConfigurationValidation) {
        editor.putInt(CONFIG_AGE_FROM, data.ageFiltering_Min)
        editor.putInt(CONFIG_AGE_TO, data.ageFiltering_Max)
        editor.putInt(CONFIG_DISTANCE_FROM, data.distanceFiltering_Min)
        editor.putInt(CONFIG_DISTANCE_TO, data.distanceFiltering_Max)

        editor.apply()
    }

    fun getSettingConfiguration() = SettingConfiguration(
        ageFiltering_Max = pref.getInt(CONFIG_AGE_TO, 85),
        ageFiltering_Min = pref.getInt(CONFIG_AGE_FROM, 14),
        distanceFiltering_Max = pref.getInt(CONFIG_DISTANCE_TO, 50),
        distanceFiltering_Min = pref.getInt(CONFIG_DISTANCE_FROM, 0)
    )

    fun removeUserSession() {

        clearSharedPreferences()

        editor.putString(USER_NAME, "")
        editor.putString(USER_IMAGE, "")
        editor.putString(USER_TOKEN, "")
        editor.putInt(INBOX_COUNT, 0)
        editor.putInt(FRIEND_REQUEST_NUMBER, 0)
        editor.putInt(NOTIFICATION_COUNT, 0)
        editor.putBoolean(USER_LOGED_IN, false)

        editor.apply()
    }

    fun clearSharedPreferences() {
        editor.clear()
        editor.apply()
    }


}