package com.hardrubic.music.util

import java.text.SimpleDateFormat
import java.util.*

object FormatUtil {
    fun formatDuration(millisecond: Long): String {
        val second = millisecond / 1000
        return String.format("%02d:%02d", second % 3600 / 60, second % 60)
    }

    fun formatPublishTime(millisecond: Long): String {
        return SimpleDateFormat("yyyy-MM-dd").format(Date(millisecond))
    }

    fun formatArtistNames(artistNames: List<String>): String {
        return artistNames.joinToString(separator = "/")
    }

    fun formatAlias(alias: List<String>): String {
        return alias.joinToString(separator = "/", prefix = "(", postfix = ")")
    }
}