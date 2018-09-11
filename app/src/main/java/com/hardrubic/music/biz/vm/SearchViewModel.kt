package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.command.AddMusicsCommand
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.service.MusicServiceControl
import io.reactivex.functions.Consumer
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(application: Application, val musicRepository: MusicRepository,
                                          val recentRepository: RecentRepository, val httpService: HttpService) : AndroidViewModel(application) {
    val musicData = MutableLiveData<List<MusicVO>>()
    val artistData = MutableLiveData<List<Artist>>()
    val albumData = MutableLiveData<List<Album>>()

    fun searchMusic(searchText: String, errorConsumer: Consumer<Throwable>) {
        if (searchText.isEmpty()) {
            musicData.value = Collections.emptyList()
        } else {
            httpService.applySearchMusic(searchText)
                    .subscribe(Consumer<List<MusicVO>> {
                        musicData.value = it
                    }, errorConsumer)
        }
    }

    fun searchArtist(searchText: String, errorConsumer: Consumer<Throwable>) {
        if (searchText.isEmpty()) {
            artistData.value = Collections.emptyList()
        } else {
            httpService.applySearchArtist(searchText)
                    .subscribe(Consumer<List<Artist>> {
                        artistData.value = it
                    }, errorConsumer)
        }
    }


    fun searchAlbum(searchText: String, errorConsumer: Consumer<Throwable>) {
        if (searchText.isEmpty()) {
            albumData.value = Collections.emptyList()
        } else {
            httpService.applySearchAlbum(searchText)
                    .subscribe(Consumer<List<Album>> {
                        albumData.value = it
                    }, errorConsumer)
        }
    }

    fun saveMusics(musics: List<Music>) {
        musicRepository.addMusic(musics)
    }

    fun playMusics(musics: List<Music>, playMusicId: Long) {
        MusicServiceControl.runInMusicService(getApplication()) {
            RemoteControl.executeCommand(AddMusicsCommand(musics, musicRepository, it))

            val playMusic = if (playMusicId != null) musicRepository.queryMusic(playMusicId) else null
            if (playMusic != null) {
                RemoteControl.executeCommand(SelectAndPlayCommand(playMusic, recentRepository, it))
            }
        }
    }
}