package com.hardrubic.music.network.response

import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.network.response.entity.NeteaseAlbum
import java.util.*

class SearchAlbumResponse : BaseResponse() {
    var result: Result? = null

    class Result {
        var albums: List<NeteaseAlbum>? = null
        var albumCount: Int = -1
    }

    fun getAlbumVOs(): List<AlbumVO> {
        return result?.albums?.map {
            val vo = AlbumVO(it.id, it.name)
            vo.picUrl = it.picUrl
            vo.alias = it.alias ?: listOf()
            vo.publishTime = it.publishTime
            vo.artistNames = it.artists?.map { it.name } ?: listOf()
            vo
        } ?: Collections.emptyList()
    }
}

