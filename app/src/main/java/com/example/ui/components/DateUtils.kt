package com.example.ui.components

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateUtils {

    private val ptLocale = Locale("pt", "BR")

    fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

    fun epochDayToLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    fun formatEpochDayToDayMonth(epochDay: Long): String {
        val localDate = LocalDate.ofEpochDay(epochDay)
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", ptLocale)
        return localDate.format(formatter)
    }

    fun formatEpochDayShort(epochDay: Long): String {
        val localDate = LocalDate.ofEpochDay(epochDay)
        val formatter = DateTimeFormatter.ofPattern("dd/MM", ptLocale)
        return localDate.format(formatter)
    }

    fun formatEpochDayWithWeekday(epochDay: Long): String {
        val localDate = LocalDate.ofEpochDay(epochDay)
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", ptLocale)
        return localDate.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(ptLocale) else it.toString() }
    }

    fun formatSecondsToTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }

    fun formatMillisToHourMinute(millis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", ptLocale)
        return sdf.format(Date(millis))
    }
}
