package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.UpdatePlayListCommand
import com.hardrubic.music.biz.component.DaggerCommonMusicListViewModelComponent
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.biz.resource.MusicDownload
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.bo.MusicResourceBO
import com.hardrubic.music.network.HttpService
import io.reactivex.functions.Consumer
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.concurrent.thread

class CommonMusicListViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository

    init {
        DaggerCommonMusicListViewModelComponent.builder().build().inject(this)
    }

    fun selectMusic(musicId: Long, errorConsumer: Consumer<Throwable>) {
        selectMusic(listOf(musicId), musicId, errorConsumer)
    }

    fun selectMusic(musicIds: List<Long>, playMusicId: Long, errorConsumer: Consumer<Throwable>) {
        applyMusicDetailAndResource(musicIds, playMusicId, errorConsumer)
    }

    private fun applyMusicDetailAndResource(musicIds: List<Long>, playMusicId: Long, errorConsumer: Consumer<Throwable>) {
        val countDownLatch = CountDownLatch(2)
        thread {
            var saveMusics = listOf<Music>()
            var musicResourceBOs = listOf<MusicResourceBO>()

            HttpService.instance.applyMusicDetail(musicIds)
                    .subscribe(Consumer {
                        saveMusics = it
                        countDownLatch.countDown()
                    }, errorConsumer)

            HttpService.instance.applyMusicResource(musicIds)
                    .subscribe(Consumer {
                        MusicDownload.downloadSync(getApplication(), it, errorConsumer)
                        musicResourceBOs = it
                        countDownLatch.countDown()
                    }, errorConsumer)

            countDownLatch.await()

            MusicDownload.fillResource2Music(saveMusics, musicResourceBOs)
            finishCacheMusics(saveMusics, playMusicId)
        }
    }

    private fun finishCacheMusics(musics: List<Music>, playMusicId: Long) {
        musicRepository.addMusic(musics)

        //添加到播放列表
        if (PlayListHelper.add(musics)) {
            val musics = PlayListHelper.list().mapNotNull { musicRepository.queryMusic(it) }
            RemoteControl.executeCommand(UpdatePlayListCommand(musics))
        }

        //play
        val playMusic = musicRepository.queryMusic(playMusicId)
        if (playMusic != null) {
            recentRepository.add(playMusic.musicId)
            RemoteControl.executeCommand(SelectAndPlayCommand(playMusic))
        }
    }
}