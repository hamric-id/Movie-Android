package com.hamric.movie_android.utils

import java.util.Locale

object LocaleUtils {

    fun getDeviceLocaleString(separator: Char = '-'): String {
        val currentLocale = Locale.getDefault()

        if (separator == '_') return currentLocale.toString()

        val language = currentLocale.language
        val country = currentLocale.country
        return if (country.isNotEmpty()) "$language$separator$country" else language
    }

    fun getDeviceCountryCode(): String = Locale.getDefault().country //ISO 3166-1
}