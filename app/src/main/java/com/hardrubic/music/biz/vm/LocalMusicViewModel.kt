package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.provider.MediaStore
import com.hardrubic.music.Constant.Companion.LOCAL_MUSIC_MINIMUM_SECOND
import com.hardrubic.music.Constant.Companion.LOCAL_MUSIC_MINIMUM_SIZE
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.UpdatePlayListCommand
import com.hardrubic.music.biz.component.DaggerLocalMusicViewModelComponent
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class LocalMusicViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository

    val localMusicData = MutableLiveData<List<Music>>()

    init {
        DaggerLocalMusicViewModelComponent.builder().build().inject(this)
    }

    fun searchLocalMusic() {

        Single.create<List<Music>> { emit ->
            emit.onSuccess(internalSearchLocalMusic())
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<List<Music>> { musics ->
                    saveLocalMusic(musics)
                    localMusicData.value = musics
                }, Consumer<Throwable> { e ->
                    localMusicData.value = Collections.emptyList()
                    e.printStackTrace()
                })
    }

    private fun saveLocalMusic(musics: List<Music>) {
        musics.forEach {
            it.local = true
        }
        musicRepository.add(musics)
    }

    fun selectMusic(musics: List<Music>) {
        musicRepository.add(musics)
        //添加到播放列表
        if (PlayListHelper.add(musics)) {
            val musics = PlayListHelper.list().mapNotNull { musicRepository.queryMusic(it) }
            RemoteControl.executeCommand(UpdatePlayListCommand(musics))
        }
        //播放
        val recentMusic = musics.first()
        recentRepository.add(recentMusic.musicId)
        RemoteControl.executeCommand(SelectAndPlayCommand(recentMusic))
    }

    private fun internalSearchLocalMusic(): List<Music> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM)
        val selectionStatement = StringBuilder().apply {
            this.append(" 1=1 ")
            this.append(" and ").append(" ${MediaStore.Audio.Media.SIZE} > $LOCAL_MUSIC_MINIMUM_SIZE ")
            this.append(" and ").append(" ${MediaStore.Audio.Media.DURATION} > $LOCAL_MUSIC_MINIMUM_SECOND ")
        }.toString()
        val paramArrayOfString = null
        val sortOrder = null

        val cursor = (getApplication() as Context).contentResolver.query(uri,
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
                    this.artistIds = listOf(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)))
                    this.artistNames = listOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                    this.albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    this.albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                }
                musics.add(music)
            } while (cursor.moveToNext())

            cursor.close()
        }
        return musics
    }
}