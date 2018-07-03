package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.component.DaggerArtistDetailViewModelComponent
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import io.reactivex.functions.Consumer
import javax.inject.Inject

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var collectionRepository: CollectionRepository

    var artistDetailData = MutableLiveData<NeteaseArtistDetail>()
    var artistHotMusicData = MutableLiveData<List<MusicVO>>()
    var artistHotAlbumData = MutableLiveData<List<AlbumVO>>()

    init {
        DaggerArtistDetailViewModelComponent.builder().build().inject(this)
    }

    fun internalQueryArtistDetail(artistId: Long, errorConsumer: Consumer<Throwable>) {
        HttpService.instance.applyArtistHotMusic(artistId)
                .subscribe(Consumer {
                    artistDetailData.value = it.artist
                    artistHotMusicData.value = it.getMusicVOs()
                }, errorConsumer)
    }

    fun internalQueryArtistHotAlbum(artistId: Long, errorConsumer: Consumer<Throwable>) {
        HttpService.instance.applyArtistHotAlbum(artistId)
                .subscribe(Consumer {
                    artistHotAlbumData.value = it
                }, errorConsumer)
    }
}