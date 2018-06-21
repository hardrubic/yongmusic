package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.R
import com.hardrubic.music.application.AppApplication
import com.hardrubic.music.biz.component.DaggerSelectCollectionViewModelComponent
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.db.dataobject.Collection
import javax.inject.Inject

class SelectCollectionViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var collectionRepository: CollectionRepository


    init {
        DaggerSelectCollectionViewModelComponent.builder().build().inject(this)
    }

    fun queryCollection(): List<Collection> {
        val addCollectionEntity = Collection().apply {
            this.name = getApplication<AppApplication>().getString(R.string.add_collection)
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