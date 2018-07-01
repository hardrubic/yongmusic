package com.hardrubic.music.network.response

import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.response.entity.NeteaseAlbumDetail
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import com.hardrubic.music.network.response.entity.NeteaseMusic2

class AlbumDetailResponse : BaseResponse() {
    var album: NeteaseAlbumDetail? = null
    var songs: List<NeteaseMusic2>? = null

    fun getMusicVOs(): List<MusicVO> {
        return songs?.map { it.getMusicVO() } ?: listOf()
    }
}

