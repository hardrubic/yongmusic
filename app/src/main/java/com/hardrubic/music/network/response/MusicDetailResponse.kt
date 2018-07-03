package com.hardrubic.music.network.response

import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.network.response.entity.NeteaseMusic2

class MusicDetailResponse : BaseResponse() {
    var songs: List<NeteaseMusic2>? = null

    fun getMusics(): List<Music> {
        return songs?.map { it.getMusic() } ?: listOf()
    }
}

