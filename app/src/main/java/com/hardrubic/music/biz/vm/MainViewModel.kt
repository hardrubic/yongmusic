package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.Constant
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Collection
import com.hardrubic.music.db.dataobject.Music
import javax.inject.Inject

class MainViewModel @Inject constructor(application: Application, val musicRepository: MusicRepository,
                                        val collectionRepository: CollectionRepository, val recentRepository: RecentRepository) : AndroidViewModel(application) {

    fun queryMusic(musicId: Long): Music? {
        return musicRepository.queryMusic(musicId)
    }

    fun queryCollection(id: Long): Collection? {
        return collectionRepository.queryCollection(id)
    }

    fun queryCollection(): List<Collection> {
        val collections = collectionRepository.queryAllCollection()
                .filterNot { it.id == Constant.LOVE_COLLECTION_ID }
        collections.forEach {
            it.musicNum = collectionRepository.queryCollectionMusicIds(it.id).size
        }
        return collections
    }

    fun queryLoveCollectionMusicNum(): Int {
        return collectionRepository.queryCollectionMusicIds(Constant.LOVE_COLLECTION_ID).size
    }

    fun queryLocalMusicNum(): Int {
        return musicRepository.queryLocalMusic().size
    }

    fun queryRecentNum(): Int {
        return recentRepository.queryRecentMusics().size
    }

    fun addCollection(name: String, id: Long? = null) {
        collectionRepository.addCollection(name, id)
    }

    fun importFrom(){

    }
}