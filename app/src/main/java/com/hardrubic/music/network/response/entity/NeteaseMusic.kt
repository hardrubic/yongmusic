package com.hardrubic.music.network.response.entity

import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.vo.MusicVO
import java.util.*

class NeteaseMusic {
    var id: Long = -1
    var name = ""
    var artists: List<NeteaseArtist>? = null
    var album: NeteaseAlbum? = null
    var duration = 0

    fun getMusic(): Music {
        val music = Music()
        music.musicId = id
        music.name = name
        music.duration = duration
        music.artistIds = artists?.map { it.id }
        music.artistNames = artists?.map { it.name }
        music.artists = getArtist()
        music.albumId = getAlbum()?.albumId
        music.albumName = getAlbum()?.name
        music.album = getAlbum()
        music.path = ""
        music.download = false
        music.local = false
        return music
    }

    fun getMusicVO(): MusicVO {
        val vo = MusicVO(id, name)
        vo.artistNames = artists?.map { it.name } ?: listOf()
        vo.albumName = album?.name ?: ""
        return vo
    }

    fun getArtist(): List<Artist> {
        return artists?.map { it.getArtist() } ?: Collections.emptyList()
    }

    fun getAlbum(): Album? {
        return album?.getAlbum()
    }
}