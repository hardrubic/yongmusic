package com.hardrubic.music.network.response.entity

import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.entity.vo.AlbumVO

class NeteaseAlbum {
    var id: Long = -1
    var name = ""
    var picUrl = ""
    var artists: List<NeteaseArtist>? = null
    var publishTime = -1L
    var alias: List<String>? = null

    fun getAlbum(): Album {
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

    fun getAlbumVO(): AlbumVO {
        val vo = AlbumVO(id, name)
        vo.picUrl = picUrl
        vo.alias = alias ?: listOf()
        vo.publishTime = publishTime
        vo.artistNames = artists?.map { it.name } ?: listOf()
        return vo
    }
}