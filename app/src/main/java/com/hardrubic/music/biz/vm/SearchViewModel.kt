package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.command.list.AddMusicsCommand
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.bo.MusicRelatedBO
import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.service.MusicServiceControl
import io.reactivex.functions.Consumer
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(application: Application, val musicRepository: MusicRepository,
                                          val recentRepository: RecentRepository, val httpService: HttpService) : AndroidViewModel(application) {
    private val SEARCH_LIMIT = 30

    val musicData = MutableLiveData<List<MusicVO>>()
    val artistData = MutableLiveData<List<Artist>>()
    val albumData = MutableLiveData<List<AlbumVO>>()
    val searchMoreEnd = MutableLiveData<Boolean>()

    private fun resetSearch() {
        searchMoreEnd.value = false
    }

    fun searchMusic(searchText: String, errorConsumer: Consumer<Throwable>) {
        resetSearch()

        if (searchText.isEmpty()) {
            musicData.value = Collections.emptyList()
        } else {
            httpService.applySearchMusic(searchText, SEARCH_LIMIT, 0)
                    .subscribe(Consumer<List<MusicVO>> {
                        musicData.value = it
                    }, errorConsumer)
        }
    }

    fun searchArtist(searchText: String, errorConsumer: Consumer<Throwable>) {
        resetSearch()

        if (searchText.isEmpty()) {
            artistData.value = Collections.emptyList()
        } else {
            httpService.applySearchArtist(searchText, SEARCH_LIMIT, 0)
                    .subscribe(Consumer<List<Artist>> {
                        artistData.value = it
                    }, errorConsumer)
        }
    }


    fun searchAlbum(searchText: String, errorConsumer: Consumer<Throwable>) {
        resetSearch()

        if (searchText.isEmpty()) {
            albumData.value = Collections.emptyList()
        } else {
            httpService.applySearchAlbum(searchText, SEARCH_LIMIT, 0)
                    .subscribe(Consumer<List<AlbumVO>> {
                        albumData.value = it
                    }, errorConsumer)
        }
    }

    fun searchMoreMusic(searchText: String, errorConsumer: Consumer<Throwable>) {
        val offset = musicData.value!!.size
        httpService.applySearchMusic(searchText, SEARCH_LIMIT, offset)
                .subscribe(Consumer<List<MusicVO>> {
                    if (it.isEmpty()) {
                        searchMoreEnd.value = true
                    } else {
                        val history = musicData.value!!.toMutableList()
                        history.addAll(it)
                        musicData.value = history
                    }
                }, errorConsumer)
    }

    fun searchMoreArtist(searchText: String, errorConsumer: Consumer<Throwable>) {
        val offset = artistData.value!!.size
        httpService.applySearchArtist(searchText, SEARCH_LIMIT, offset)
                .subscribe(Consumer<List<Artist>> {
                    if (it.isEmpty()) {
                        searchMoreEnd.value = true
                    } else {
                        val history = artistData.value!!.toMutableList()
                        history.addAll(it)
                        artistData.value = history
                    }
                }, errorConsumer)
    }

    fun searchMoreAlbum(searchText: String, errorConsumer: Consumer<Throwable>) {
        val offset = albumData.value!!.size
        httpService.applySearchAlbum(searchText, SEARCH_LIMIT, offset)
                .subscribe(Consumer<List<AlbumVO>> {
                    if (it.isEmpty()) {
                        searchMoreEnd.value = true
                    } else {
                        val history = albumData.value!!.toMutableList()
                        history.addAll(it)
                        albumData.value = history
                    }
                }, errorConsumer)
    }

    fun saveMusicRelated(relatedBO: List<MusicRelatedBO>) {
        musicRepository.saveMusicRelated(relatedBO)
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