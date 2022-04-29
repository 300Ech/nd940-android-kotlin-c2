package com.udacity.asteroidradar.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("NewApi", "WeekBasedYear")
fun getCurrentWeekDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()
    val calendar = Calendar.getInstance()
    calendar[Calendar.DAY_OF_WEEK] = Calendar.MONDAY

    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    for (i in 0..6) {
        formattedDateList.add(dateFormat.format(calendar.time))
        calendar.add(Calendar.DATE, 1)
    }
    return formattedDateList
}