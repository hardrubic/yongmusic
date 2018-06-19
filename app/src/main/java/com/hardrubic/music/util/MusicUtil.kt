package com.hardrubic.music.util

import android.content.Context
import android.provider.MediaStore
import com.hardrubic.music.db.dataobject.Music

object MusicUtil {
    private const val MINIMUM_SIZE = 1024 * 1024 //1M
    private const val MINIMUM_SECOND = 60 * 1000 //1分钟

    fun searchLocalMusic(context: Context): List<Music> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ARTIST)
        val selectionStatement = StringBuilder().apply {
            this.append(" 1=1 ")
            this.append(" and ").append(" ${MediaStore.Audio.Media.SIZE} > $MINIMUM_SIZE ")
            this.append(" and ").append(" ${MediaStore.Audio.Media.DURATION} > $MINIMUM_SECOND ")
        }.toString()
        val paramArrayOfString = null
        val sortOrder = null

        val cursor = context.contentResolver.query(uri,
                projection, selectionStatement, paramArrayOfString, sortOrder)

        val musics = mutableListOf<Music>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val music = Music().apply {
                    this.musicId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    this.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    this.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    this.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    this.size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    this.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                }
                musics.add(music)
            } while (cursor.moveToNext())

            cursor.close()
        }
        return musics
    }
}