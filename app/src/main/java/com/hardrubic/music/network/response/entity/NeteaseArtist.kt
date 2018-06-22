package com.hardrubic.music.network.response.entity

import com.hardrubic.music.db.dataobject.Artist

class NeteaseArtist {
    var name = ""
    var id: Long = -1
    var picUrl = ""
    var alias: List<String>? = null

    fun getArtist(): Artist {
        val artist = Artist()
        artist.artistId = id
        artist.name = name
        artist.picUrl = picUrl
        artist.alias = alias
        return artist
    }
}