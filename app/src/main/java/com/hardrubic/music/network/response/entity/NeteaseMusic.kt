package com.hardrubic.music.network.response.entity

import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import java.util.*

class NeteaseMusic {
    var id: Long = -1
    var name = ""
    var artists: List<NeteaseArtist>? = null
    var album: NeteaseAlbum? = null
    var duration = 0

    fun getArtist(): List<Artist> {
        return artists?.map { it.getArtist() } ?: Collections.emptyList()
    }

    fun getAlbum(): Album? {
        return album?.getAlbum()
    }
}