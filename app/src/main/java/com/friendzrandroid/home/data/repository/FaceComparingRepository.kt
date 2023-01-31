package com.friendzrandroid.home.data.repository

import android.app.Activity
import com.amazonaws.services.rekognition.model.CompareFacesResult
import com.friendzrandroid.utils.FaceDetecting

class FaceComparingRepository {

    fun getComparingResult(
        activity: Activity,
        accessKey: String,
        secretKey: String,
        firstImage: String,
        secondImage: String
    ): CompareFacesResult? {
        return FaceDetecting(activity, accessKey, secretKey).detect(firstImage, secondImage)
    }
}