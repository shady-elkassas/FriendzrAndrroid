package com.friendzrandroid.home.data.model

import com.friendzrandroid.home.data.model.enum.GenderEnum
import java.io.Serializable

data class UserProfileData(
    val age: Int,
    val userImages: List<String>?= null,
    val myAppearanceTypes: List<Int>,
    val allowmylocation: Boolean,
    val bio: String?,
    val birthdate: String,
    val email: String,
    val facebook: String? = null,
    val gender: String?,
    val otherGenderName: String?,
    val displayedUserName: String,
    val ghostmode: Boolean,
    val instagram: String? = null,
    val key: Int,
    val lang: String?,
    val lat: String?,
    val linkAccountmodel: Any? = null,
    val listoftagsmodel: List<TagsModel>,
    val iamList: List<TagsModel>,
    val prefertoList: List<TagsModel>,
    val needUpdate: Int,
    val pushnotification: Boolean,
    val snapchat: String? = null,
    val tiktok: String? = null,
    val userImage: String,
    val userName: String,
    val userid: String? = null,
    val universityCode: String? = null,

    ) : Serializable {

    val isMale: GenderEnum
        get() {

            if (gender.isNullOrEmpty()) {
                return GenderEnum.NULL
            } else {
                if (gender.lowercase().equals("male")) {
                    return GenderEnum.MALE
                } else if (gender.lowercase().equals("female")) {
                    return GenderEnum.FEMALE
                } else {
                    return GenderEnum.OTHER
                }
            }

        }
}
