package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.hardrubic.music.R
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.db.dataobject.Collection
import javax.inject.Inject

class SelectCollectionViewModel @Inject constructor(val application: Application, val musicRepository: MusicRepository,
                                                    val collectionRepository: CollectionRepository) : ViewModel() {

    fun queryCollection(): List<Collection> {
        val addCollectionEntity = Collection().apply {
            this.name = application.getString(R.string.add_collection)
            this.musicNum = -1
        }

        val collections = collectionRepository.queryAllCollection().toMutableList()
        collections.forEach {
            it.musicNum = collectionRepository.queryCollectionMusicIds(it.id).size
        }

        collections.add(0, addCollectionEntity)

        return collections
    }

    fun addCollection(name: String, id: Long? = null): Long {
        return collectionRepository.addCollection(name, id)
    }
}