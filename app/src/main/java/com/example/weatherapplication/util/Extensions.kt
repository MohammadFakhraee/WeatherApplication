package com.example.weatherapplication.util

import android.content.Context
import android.icu.util.Calendar
import com.example.weatherapplication.R
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

fun Int.formatDay(): String = "%02d".format(this)

fun Int.formatYear(): String = "%04d".format(this)

fun String.toCalendar(pattern: String): Calendar {
    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
    return Calendar.getInstance().also { it.time = sdf.parse(this) }
}

fun Calendar.month(): String = DateFormatSymbols().months[get(Calendar.MONTH)]

fun Calendar.dayOfWeek(): String = stringDayOfWeek(get(Calendar.DAY_OF_WEEK))

fun Calendar.day(): String = get(Calendar.DAY_OF_MONTH).formatDay()

fun Calendar.year(): String = get(Calendar.YEAR).formatYear()

fun Calendar.stylishDate(context: Context): String = context.getString(R.string.showing_date, month(), day(), year())

fun stringDayOfWeek(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        2 -> "Monday"
        3 -> "Tuesday"
        4 -> "Wednesday"
        5 -> "Thursday"
        6 -> "Friday"
        7 -> "Saturday"
        1 -> "Sunday"
        else -> throw IllegalArgumentException("Invalid value for dayOfWeek: $dayOfWeek")
    }
}