package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.provider.MediaStore
import com.hardrubic.music.Constant.Companion.LOCAL_MUSIC_MINIMUM_SECOND
import com.hardrubic.music.Constant.Companion.LOCAL_MUSIC_MINIMUM_SIZE
import com.hardrubic.music.biz.adapter.MusicEntityAdapter
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.vo.MusicVO
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class LocalMusicViewModel @Inject constructor(val application: Application,
                                              val musicRepository: MusicRepository, val recentRepository: RecentRepository) : ViewModel() {

    val localMusicData = MutableLiveData<List<MusicVO>>()

    fun searchLocalMusic() {

        Single.create<List<MusicVO>> { emit ->
            val musics = internalSearchLocalMusic()
            saveLocalMusic(musics)
            val musicVOs = musics.map { MusicEntityAdapter.toMusicVO(it) }
            emit.onSuccess(musicVOs)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<List<MusicVO>> {
                    localMusicData.value = it
                }, Consumer<Throwable> { e ->
                    localMusicData.value = Collections.emptyList()
                    e.printStackTrace()
                })
    }

    private fun saveLocalMusic(musics: List<Music>) {
        musics.forEach {
            it.local = true
        }
        musicRepository.addMusic(musics)
    }

    private fun internalSearchLocalMusic(): List<Music> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM)
        val selectionStatement = StringBuilder().apply {
            this.append(" 1=1 ")
            this.append(" and ").append(" ${MediaStore.Audio.Media.SIZE} > $LOCAL_MUSIC_MINIMUM_SIZE ")
            this.append(" and ").append(" ${MediaStore.Audio.Media.DURATION} > $LOCAL_MUSIC_MINIMUM_SECOND ")
        }.toString()
        val paramArrayOfString = null
        val sortOrder = null

        val cursor = (application as Context).contentResolver.query(uri,
                projection, selectionStatement, paramArrayOfString, sortOrder)

        val musics = mutableListOf<Music>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val music = Music().apply {
                    this.musicId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    this.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    this.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    this.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    this.artistIds = listOf(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)))
                    this.artistNames = listOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                    this.albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    this.albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                }
                musics.add(music)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return musics
    }
}