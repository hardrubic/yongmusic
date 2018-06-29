package com.hardrubic.music.network.response.entity

import com.hardrubic.music.db.dataobject.Album

class NeteaseAlbum {
    var id: Long = -1
    var name = ""
    var picUrl = ""
    var artists: List<NeteaseArtist>? = null
    var publishTime = -1L
    var alias: List<String>? = null

    fun getAlbum():Album{
        val album = Album()
        album.albumId = id
        album.name = name
        album.picUrl = picUrl
        album.alias = alias
        album.publishTime = publishTime
        album.artists = artists?.map { it.getArtist() }
        album.artistIds = artists?.map { it.id }
        album.artistNames = artists?.map { it.name }
        return album
    }
}