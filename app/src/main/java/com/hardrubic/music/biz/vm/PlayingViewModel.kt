package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.Constant
import com.hardrubic.music.biz.command.NextCommand
import com.hardrubic.music.biz.command.PreviousCommand
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.component.DaggerPlayingViewModelComponent
import com.hardrubic.music.biz.repository.CollectionRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Collection
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl
import javax.inject.Inject

class PlayingViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository
    @Inject
    lateinit var collectionRepository: CollectionRepository

    var playingMusic: Music? = null

    init {
        DaggerPlayingViewModelComponent.builder().build().inject(this)
    }

    fun existPlayingMusic(): Boolean {
        return playingMusic != null
    }

    fun queryMusic(musicId: Long): Music? {
        return musicRepository.queryMusic(musicId)
    }

    fun previousMusic(musicServiceControl: MusicServiceControl) {
        RemoteControl.executeCommand(PreviousCommand(musicServiceControl))
    }

    fun nextMusic(musicServiceControl: MusicServiceControl) {
        RemoteControl.executeCommand(NextCommand(musicServiceControl))
    }

    fun isMusicLove(musicId: Long): Boolean {
        return collectionRepository.checkMusicInCollection(musicId, Constant.LOVE_COLLECTION_ID)
    }

    fun changeMusicLove(musicId: Long, love: Boolean) {
        if (love) {
            collectionRepository.addMusicToCollection(musicId, Constant.LOVE_COLLECTION_ID)
        } else {
            collectionRepository.deleteMusicFromCollection(musicId, Constant.LOVE_COLLECTION_ID)
        }
    }

    fun queryCollection(id: Long): Collection? {
        return collectionRepository.queryCollection(id)
    }

    fun addOrDelete2Collection(musicId: Long, collectionId: Long, flag: Boolean) {
        if (flag) {
            collectionRepository.addMusicToCollection(musicId, collectionId)
        } else {
            collectionRepository.deleteMusicFromCollection(musicId, collectionId)
        }
    }

    fun queryArtist(id: Long): Artist? {
        return musicRepository.queryArtist(id)
    }

    fun queryAlbum(id: Long): Album? {
        return musicRepository.queryAlbum(id)
    }
}