package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.biz.adapter.MusicEntityAdapter
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.UpdatePlayListCommand
import com.hardrubic.music.biz.component.DaggerCollectionDetailViewModelComponent
import com.hardrubic.music.biz.helper.PlayListHelper
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

    fun selectMusic(musicId: Long) {
        selectMusic(listOf(musicId), musicId)
    }

    fun selectMusic(musicIds: List<Long>, playMusicId: Long? = null) {
        val musics = musicRepository.queryMusic(musicIds)

        //添加到播放列表
        PlayListHelper.replace(musics)
        RemoteControl.executeCommand(UpdatePlayListCommand(musics))

        //play
        val playMusic = if (playMusicId == null) {
            musics.first()
        } else {
            musics.find { it.musicId == playMusicId }
        }
        if (playMusic != null) {
            recentRepository.add(playMusic.musicId)
            RemoteControl.executeCommand(SelectAndPlayCommand(playMusic))
        }
    }
}