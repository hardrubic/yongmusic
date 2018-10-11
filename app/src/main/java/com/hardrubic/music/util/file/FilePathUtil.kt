package com.hardrubic.music.util.file

import android.content.Context
import android.os.Environment
import com.hardrubic.music.Constant
import java.io.File

object FilePathUtil {
    fun getAppDir(context: Context): String {
        return "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}${Constant.APP_NAME}${File.separator}"
    }

    fun getMusicCacheDir(context: Context): String {
        return getAppDir(context) + "cache" + File.separator
    }
}