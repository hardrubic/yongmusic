package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.hardrubic.music.biz.adapter.MusicEntityAdapter
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Collection
import com.hardrubic.music.entity.vo.MusicVO
import javax.inject.Inject

class CollectionDetailViewModel @Inject constructor(val application: Application, val musicRepository: MusicRepository,
                                                    val collectionRepository: CollectionRepository, val recentRepository: RecentRepository) : ViewModel() {

    fun queryCollection(collectionId: Long): Collection? {
        return collectionRepository.queryCollection(collectionId)
    }

    fun queryCollectionMusicVOs(collectionId: Long): List<MusicVO> {
        val musicIds = collectionRepository.queryCollectionMusicIds(collectionId)
        val musics = musicRepository.queryMusic(musicIds)
        return musics.map { MusicEntityAdapter.toMusicVO(it) }
    }
}