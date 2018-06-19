package com.hardrubic.music.util

object FormatUtil {
    fun formatDuration(millisecond: Int): String {
        val second = millisecond / 1000
        return String.format("%02d:%02d", second % 3600 / 60, second % 60)
    }
}