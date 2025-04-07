package ru.andrewvhub.utils.extension

import android.content.res.Resources
import android.util.TypedValue
import ru.andrewvhub.usagetime.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

val Int.dp: Int
    get() = ceil(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        )
    ).toInt()

fun Long.formatTime(resources: Resources): String {
    val totalMilliseconds = this
    val totalMinutes = totalMilliseconds / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        totalMilliseconds == 0L -> resources.getString(R.string.time_zero)
        totalMilliseconds < 60000 -> resources.getString(R.string.time_less_than_minute)
        hours > 0 -> resources.getString(R.string.time_hours_minutes, hours, minutes)
        else -> resources.getString(R.string.time_minutes, minutes)
    }
}

fun Long.getIndexDayOfWeekNumberFromTimestamp(): Int {
    val localDate = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val weekFields = WeekFields.of(Locale.getDefault())
    return localDate.get(weekFields.dayOfWeek()) - 1
}

fun Long.toDateString(
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
        "dd.MM.yyyy",
        Locale.getDefault()
    )
): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return localDateTime.format(dateFormatter)
}

fun Long.formatDate(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("E, d MMM", Locale("ru"))
    return sdf.format(date).toString()
}