package com.hardrubic.music.network.response

import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.network.response.entity.NeteaseAlbum
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail

class ArtistHotAlbumResponse : BaseResponse() {
    var artist: NeteaseArtistDetail? = null
    var hotAlbums: List<NeteaseAlbum>? = null

    fun getAlbumVOs(): List<AlbumVO> {
        return hotAlbums?.map { it.getAlbumVO() } ?: listOf()
    }
}

