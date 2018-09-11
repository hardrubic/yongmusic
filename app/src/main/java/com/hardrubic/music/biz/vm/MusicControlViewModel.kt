package com.hardrubic.music.biz.vm

import android.arch.lifecycle.ViewModel
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Music
import javax.inject.Inject

class MusicControlViewModel @Inject constructor(val musicRepository: MusicRepository) : ViewModel() {
    fun queryMusic(musicId: Long): Music? {
        return musicRepository.queryMusic(musicId)
    }

    fun queryAlbum(id: Long): Album? {
        return musicRepository.queryAlbum(id)
    }

}