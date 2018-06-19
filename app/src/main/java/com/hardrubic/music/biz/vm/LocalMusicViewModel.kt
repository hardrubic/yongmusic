package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.UpdatePlayListCommand
import com.hardrubic.music.biz.component.DaggerLocalMusicViewModelComponent
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.util.MusicUtil
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

    public val localMusicData = MutableLiveData<List<Music>>()

    init {
        DaggerLocalMusicViewModelComponent.builder().build().inject(this)
    }

    fun searchLocalMusic() {

        Single.create<List<Music>> { emit ->
            val localMusics = MusicUtil.searchLocalMusic(getApplication())
            emit.onSuccess(localMusics)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<List<Music>> { musics ->
                    localMusicData.value = musics
                }, Consumer<Throwable> { e ->
                    localMusicData.value = Collections.emptyList()
                    e.printStackTrace()
                })
    }

    fun selectLocalMusic(musics: List<Music>) {
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
}