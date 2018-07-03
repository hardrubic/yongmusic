package com.hardrubic.music.biz.vm

import android.arch.lifecycle.ViewModel
import com.hardrubic.music.biz.component.DaggerMusicControlViewModelComponent
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Music
import javax.inject.Inject

class MusicControlViewModel : ViewModel() {
    @Inject
    lateinit var musicRepository: MusicRepository

    init {
        DaggerMusicControlViewModelComponent.builder().build().inject(this)
    }

    fun queryMusic(musicId: Long): Music? {
        return musicRepository.queryMusic(musicId)
    }

    fun queryAlbum(id: Long): Album? {
        return musicRepository.queryAlbum(id)
    }

}