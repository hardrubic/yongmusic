package com.hardrubic.music.biz.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import io.reactivex.functions.Consumer
import javax.inject.Inject

class ArtistDetailViewModel @Inject constructor(val musicRepository: MusicRepository, val httpService: HttpService) : ViewModel() {

    var artistDetailData = MutableLiveData<NeteaseArtistDetail>()
    var artistHotMusicData = MutableLiveData<List<MusicVO>>()
    var artistHotAlbumData = MutableLiveData<List<AlbumVO>>()

    fun internalQueryArtistDetail(artistId: Long, errorConsumer: Consumer<Throwable>) {
        httpService.applyArtistHotMusic(artistId)
                .subscribe(Consumer {
                    artistDetailData.value = it.artist
                    artistHotMusicData.value = it.getMusicVOs()
                }, errorConsumer)
    }

    fun internalQueryArtistHotAlbum(artistId: Long, errorConsumer: Consumer<Throwable>) {
        httpService.applyArtistHotAlbum(artistId)
                .subscribe(Consumer {
                    artistHotAlbumData.value = it
                }, errorConsumer)
    }
}