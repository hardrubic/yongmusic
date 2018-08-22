package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.command.AddMusicsCommand
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.component.DaggerSearchViewModelComponent
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.biz.resource.MusicDownload
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.bo.MusicResourceBO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.service.MusicServiceControl
import io.reactivex.functions.Consumer
import java.util.*
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.concurrent.thread

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository

    val musicData = MutableLiveData<List<MusicVO>>()
    val artistData = MutableLiveData<List<Artist>>()
    val albumData = MutableLiveData<List<Album>>()

    init {
        DaggerSearchViewModelComponent.builder().build().inject(this)
    }

    fun searchMusic(searchText: String, errorConsumer: Consumer<Throwable>) {
        if (searchText.isEmpty()) {
            musicData.value = Collections.emptyList()
        } else {
            HttpService.instance.applySearchMusic(searchText)
                    .subscribe(Consumer<List<MusicVO>> {
                        musicData.value = it
                    }, errorConsumer)
        }
    }

    fun searchArtist(searchText: String, errorConsumer: Consumer<Throwable>) {
        if (searchText.isEmpty()) {
            artistData.value = Collections.emptyList()
        } else {
            HttpService.instance.applySearchArtist(searchText)
                    .subscribe(Consumer<List<Artist>> {
                        artistData.value = it
                    }, errorConsumer)
        }
    }

    fun searchAlbum(searchText: String, errorConsumer: Consumer<Throwable>) {
        if (searchText.isEmpty()) {
            albumData.value = Collections.emptyList()
        } else {
            HttpService.instance.applySearchAlbum(searchText)
                    .subscribe(Consumer<List<Album>> {
                        albumData.value = it
                    }, errorConsumer)
        }
    }

    fun selectMusic(musicId: Long, errorConsumer: Consumer<Throwable>) {
        applyMusicDetailAndResource(listOf(musicId), musicId, errorConsumer)
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
        val playMusic = musicRepository.queryMusic(playMusicId)

        MusicServiceControl.runInMusicService(getApplication()) {
            RemoteControl.executeCommand(AddMusicsCommand(musics, musicRepository, it))
            if (playMusic != null) {
                RemoteControl.executeCommand(SelectAndPlayCommand(playMusic, recentRepository, it))
            }
        }
    }
}