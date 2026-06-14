package com.hamric.movie_android.utils

import java.util.Locale

object LocaleUtils {

    fun Locale.toString(separator: Char): String {
        if (separator == '_') return this.toString()

        val language = this.language
        val country = this.country
        return if (country.isNotEmpty()) "$language$separator$country" else language
    }
}