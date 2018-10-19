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
        val dir = "${getAppDir(context)}cache${File.separator}"
        createDir(dir)
        return dir
    }

    fun getRecordAudioDir(context: Context): String {
        val dir = "${getAppDir(context)}record${File.separator}"
        createDir(dir)
        return dir
    }

    fun getRecordAudioTmpFile(context: Context): String {
        return "${getRecordAudioDir(context)}tmpRecord.tmp"
    }

    private fun createDir(dir: String) {
        val fileDir = File(dir)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
    }
}