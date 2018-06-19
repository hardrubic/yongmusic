package com.hardrubic.music.util

import android.content.Context
import android.os.Environment
import java.io.File

object FileUtil {
    fun getAppDir(context: Context): String {
        return "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}absMusic${File.separator}"
    }

    fun getMusicCacheDir(context: Context): String {
        return getAppDir(context) + "cache" + File.separator
    }
}