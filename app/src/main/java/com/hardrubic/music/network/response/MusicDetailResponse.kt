package com.hardrubic.music.network.response

import com.hardrubic.music.entity.bo.MusicRelatedBO
import com.hardrubic.music.network.response.entity.NeteaseMusic2

class MusicDetailResponse : BaseResponse() {
    var songs: List<NeteaseMusic2>? = null

    fun getMusicRelated(): List<MusicRelatedBO> {
        return songs?.map { it.getMusic() } ?: listOf()
    }
}

