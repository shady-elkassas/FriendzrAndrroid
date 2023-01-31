package com.friendzrandroid.core.utils

import java.text.SimpleDateFormat
import java.util.*

object Constants {

//    val dateFormat = "dd-MM-yyyy"

        val dateFormat = "yyyy-MM-dd"
    val timeFormat = "H:mm:ss"
    val DisplaytimeFormat = "H:mm"

    fun FormatToDate(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        format: String = Constants.dateFormat
    ): Pair<Date, String> {
        val dat = Calendar.getInstance()
        dat.set(Calendar.YEAR, year)
        dat.set(Calendar.MONTH, month)
        dat.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val strDate = SimpleDateFormat(format)
        val formatDate = strDate.format(dat.time)
        return Pair(dat.time, formatDate)
    }


    fun FormatToTime(
        hourOfDay: Int,
        minute: Int,
        format: String = Constants.timeFormat
    ): Pair<Date, String> {


        val dat = Calendar.getInstance()
        dat.set(Calendar.HOUR_OF_DAY, hourOfDay)
        dat.set(Calendar.MINUTE, minute)

        val strDate = SimpleDateFormat(format)
        val formatDate = strDate.format(dat.time)

        return Pair(dat.time, formatDate)
    }


}