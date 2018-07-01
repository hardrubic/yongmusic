package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.component.DaggerArtistDetailViewModelComponent
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.network.response.ArtistHotMusicResponse
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import io.reactivex.functions.Consumer
import javax.inject.Inject

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var collectionRepository: CollectionRepository

    public var artistDetailData = MutableLiveData<ArtistHotMusicResponse>()

    init {
        DaggerArtistDetailViewModelComponent.builder().build().inject(this)
    }

    fun internalQueryArtistDetail(artistId: Long, errorConsumer: Consumer<Throwable>) {
        HttpService.instance.applyArtistHotMusic(artistId)
                .subscribe(Consumer {
                    artistDetailData.value = it
                }, errorConsumer)
    }
}