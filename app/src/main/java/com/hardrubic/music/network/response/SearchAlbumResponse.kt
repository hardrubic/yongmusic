package com.hardrubic.music.network.response

import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.network.response.entity.NeteaseAlbum
import java.util.*

class SearchAlbumResponse : BaseResponse() {
    var result: Result? = null

    class Result {
        var albums: List<NeteaseAlbum>? = null
        var albumCount: Int = -1
    }

    fun getAlbums(): List<Album> {
        return result?.albums?.map {
            val album = Album()
            album.albumId = it.id
            album.name = it.name
            album.artistIds = it.artists?.map { it.id }
            album.artistNames = it.artists?.map { it.name }
            album.publishTime = it.publishTime
            album.alias = it.alias
            album.picUrl = it.picUrl
            album
        } ?: Collections.emptyList()
    }
}

