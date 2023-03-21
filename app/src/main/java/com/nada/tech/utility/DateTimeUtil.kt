package com.nada.tech.utility

import android.text.format.DateFormat
import android.text.format.DateUtils
import org.joda.time.DateTime
import org.joda.time.Days
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    val EEEMMMDDYYYYHHMM = SimpleDateFormat("EEE, MMM dd, yyyy h:mm a", Locale.ENGLISH)
    val EEEDDMMYYYY = SimpleDateFormat("EEE dd MMM yyyy", Locale.ENGLISH)
    val EEEEDDMMMMYYYY = SimpleDateFormat("EEEE, dd MMMM, yyyy", Locale.ENGLISH)
    val EEEDDMMHHMM = SimpleDateFormat("EEE, dd MMM, h:mm a", Locale.ENGLISH)
    val DDMMMMYYYY = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
    val HHMMSS = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    val HHMMA = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val HMM = SimpleDateFormat("h:mm a", Locale.ENGLISH)
    val DDMMMYYYYHHMMAA = SimpleDateFormat("dd MMM yyyy h:mm aa", Locale.ENGLISH)
    val DDMMYYYYHHMMAA = SimpleDateFormat("dd-MM-yyyy h:mm a", Locale.ENGLISH)

    val YYYYMMDD = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val DDMMYYYY = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    val DDMMMYYYY = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    val DDMMYYYYEEEE = SimpleDateFormat("dd/MM/yyyy EEEE", Locale.ENGLISH)
    val UTC_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    const val MAX = -1
    const val MIN = 1
    const val NORMAL = 0

    const val ONE_DAY: Long = 86400000
    val timeZone = Calendar.getInstance().timeZone

    fun currentDate(format: SimpleDateFormat): String {
        return format.format(Calendar.getInstance().time)
    }

    fun isToday(date: Long): Boolean {
        return DateUtils.isToday(date)
    }

    fun isToday(date: String, sourceFormat: SimpleDateFormat = UTC_FORMAT): Boolean {
        return DateUtils.isToday(sourceFormat.parse(date)?.time ?: 0L)
    }

    fun isTomorrow(date: String, sourceFormat: SimpleDateFormat = UTC_FORMAT): Boolean {
        val localDateTime = getFormattedDateTimeFromUtc(date, sourceFormat)
        val days = getDayCount(localDateTime.toDate(sourceFormat)?.time)
        return days == 1
    }

    fun getCalendar(time: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)
        return calendar
    }

    fun getInSecond(date: Long): Long {
        return date / 1000
    }

    fun getFormattedDateTime(value: Long?, simpleDateFormat: SimpleDateFormat): String {
        return simpleDateFormat.format(Date(value ?: 0))
    }

    fun getFormattedDateTimeFromUtc(
        mDate: String?,
        newPattern: SimpleDateFormat
    ): String {
        if (mDate.isNullOrEmpty()) return ""
        try {
            UTC_FORMAT.timeZone = TimeZone.getTimeZone("UTC");
            val utcDate = UTC_FORMAT.parse(mDate) ?: return ""
            newPattern.timeZone = TimeZone.getDefault()
            return newPattern.format(utcDate)
        } catch (e: ParseException) {
            return ""
        }
    }

    fun getRelativeDateTime(value: Long?, format: SimpleDateFormat = DDMMYYYYHHMMAA): String {
        val startDate = DateTime(value)
        val today = DateTime()
        val days: Int = Days.daysBetween(
            today.withTimeAtStartOfDay(),
            startDate.withTimeAtStartOfDay()
        ).days

        val date = when (days) {
            -1 -> "Yesterday"
            0 -> "Today"
            1 -> "Tomorrow"
            else -> return getFormattedDateTime(value, format)
        }
        return date //+ " " + getFormattedDateTime(value, HMM)
    }

    fun getDuration(startTime: String?, endTime: String?, format: SimpleDateFormat): String {
        if (startTime.isNullOrEmpty() || endTime.isNullOrEmpty()) return ""
        var different = (endTime.toDate(format)?.time ?: 0) - (startTime.toDate(format)?.time ?: 0)

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays: Long = different / daysInMilli
        different %= daysInMilli

        val elapsedHours: Long = different / hoursInMilli
        different %= hoursInMilli

        val elapsedMinutes: Long = different / minutesInMilli
        different %= minutesInMilli

        val elapsedSeconds: Long = different / secondsInMilli

        val stringBuilder = StringBuilder()
        if (elapsedDays > 0) stringBuilder.append("$elapsedDays Days ")
        if (elapsedHours > 0) stringBuilder.append("$elapsedHours Hours ")
        if (elapsedMinutes > 0) stringBuilder.append("$elapsedMinutes Minutes ")
        if (elapsedSeconds > 0) stringBuilder.append("$elapsedSeconds Seconds")
        return stringBuilder.toString().trim()
    }


    fun getDayCount(value: Long?): Int {
        if (value == null) return 0
        val startDate = DateTime(value)
        val today = DateTime()
        return Days.daysBetween(today.withTimeAtStartOfDay(), startDate.withTimeAtStartOfDay()).days
    }

    fun String.toDate(format: SimpleDateFormat = DDMMYYYYHHMMAA): Date? {
        return format.parse(this)
    }

    fun Long.fromUTC(): Long {
        if (this == 0L) return 0
        return this + timeZone.getOffset(System.currentTimeMillis())
    }

    fun Long.toUTC(): Long {
        if (this == 0L) return 0
        return this - timeZone.getOffset(this)
    }

    fun getFormattedDate(time: Long): String {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = time
        val now = Calendar.getInstance()
        val timeFormatString = ", MMMM d"
        val dateTimeFormatString = "EEEE, MMMM d"
        val HOURS = (60 * 60 * 60).toLong()
        return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
            "Today" + DateFormat.format(timeFormatString, smsTime)
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else {
            DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString()
        }
    }

    fun clearTime(time: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }

    fun changeFormat(
        mDate: String?,
        currentFormat: SimpleDateFormat,
        newFormat: SimpleDateFormat
    ): String {
        if (mDate.isNullOrEmpty()) return ""
        try {
            val cDate = currentFormat.parse(mDate) ?: ""
            return newFormat.format(cDate)
        } catch (e: ParseException) {
            return ""
        }
    }
}