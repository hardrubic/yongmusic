package com.hardrubic.music.biz.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.network.response.entity.NeteaseAlbumDetail
import io.reactivex.functions.Consumer
import javax.inject.Inject

class AlbumDetailViewModel @Inject constructor(val httpService: HttpService) : ViewModel() {

    var albumDetailData = MutableLiveData<NeteaseAlbumDetail>()
    var albumMusicData = MutableLiveData<List<MusicVO>>()

    fun internalQueryAlbumDetail(albumId: Long, errorConsumer: Consumer<Throwable>) {
        httpService.applyAlbumDetail(albumId)
                .subscribe(Consumer {
                    albumDetailData.value = it.album
                    albumMusicData.value = it.getMusicVOs()
                }, errorConsumer)
    }
}