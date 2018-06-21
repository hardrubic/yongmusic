package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.UpdatePlayListCommand
import com.hardrubic.music.biz.component.DaggerCollectionDetailViewModelComponent
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Collection
import com.hardrubic.music.db.dataobject.Music
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

    fun queryCollectionMusics(collectionId: Long): List<Music> {
        val musicIds = collectionRepository.queryCollectionMusicIds(collectionId)
        return musicRepository.queryMusic(musicIds)
    }

    fun selectMusic(musics: List<Music>, playMusic: Music? = null) {
        //添加到播放列表
        PlayListHelper.replace(musics)
        RemoteControl.executeCommand(UpdatePlayListCommand(musics))
        //播放
        val recentMusic = playMusic ?: musics.first()
        recentRepository.add(recentMusic.musicId)
        RemoteControl.executeCommand(SelectAndPlayCommand(recentMusic))
    }

}