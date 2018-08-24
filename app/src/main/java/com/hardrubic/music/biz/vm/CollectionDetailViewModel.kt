package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.biz.adapter.MusicEntityAdapter
import com.hardrubic.music.biz.component.DaggerCollectionDetailViewModelComponent
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Collection
import com.hardrubic.music.entity.vo.MusicVO
import javax.inject.Inject

class CollectionDetailViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var collectionRepository: CollectionRepository
    @Inject
    lateinit var recentRepository: RecentRepository

    init {
        DaggerCollectionDetailViewModelComponent.builder().build().inject(this)
    }

    fun queryCollection(collectionId: Long): Collection? {
        return collectionRepository.queryCollection(collectionId)
    }

    fun queryCollectionMusicVOs(collectionId: Long): List<MusicVO> {
        val musicIds = collectionRepository.queryCollectionMusicIds(collectionId)
        val musics = musicRepository.queryMusic(musicIds)
        return musics.map { MusicEntityAdapter.toMusicVO(it) }
    }
}