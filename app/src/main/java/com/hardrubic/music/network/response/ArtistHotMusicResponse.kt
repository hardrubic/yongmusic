package com.hardrubic.music.network.response

import com.google.gson.annotations.SerializedName
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import com.hardrubic.music.network.response.entity.NeteaseMusic2

class ArtistHotMusicResponse : BaseResponse() {
    var artist: NeteaseArtistDetail? = null
    var hotSongs: List<NeteaseMusic2>? = null

    fun getMusicVOs(): List<MusicVO> {
        return hotSongs?.map { it.getMusicVO() } ?: listOf()
    }
}

