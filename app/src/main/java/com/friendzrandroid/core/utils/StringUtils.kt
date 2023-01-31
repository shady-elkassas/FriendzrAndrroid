package com.friendzrandroid.core.utils

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*


fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


fun Date.FormatToDate(format: String = Constants.dateFormat): String {

    val strDate = SimpleDateFormat(format, Locale.ENGLISH)
    val formatDate = strDate.format(this)
    return formatDate
}


fun Date.FormatToTime(
    format: String = Constants.timeFormat
): String {

    val strDate = SimpleDateFormat(format, Locale.ENGLISH)
    val formatDate = strDate.format(this)

    return formatDate
}


fun Double.toKm() = this * 1.60934
fun Double.toMiles() = this / 1.60934
