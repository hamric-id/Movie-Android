package com.hamric.movie_android.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import java.time.format.DateTimeFormatter

object DateUtils {

    fun String.toLocalDate(pattern: String): LocalDate {
        if (pattern == "yyyy-MM-dd'T'HH:mm:ss.SSSX")
            return Instant.parse(this)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDate.parse(this, formatter)
    }

    fun LocalDate.toString(pattern: String, locale: Locale = Locale.getDefault()): String{
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return this.format(formatter)
    }

}