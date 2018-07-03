package com.hardrubic.music.network.response.entity

import com.google.gson.annotations.SerializedName
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.vo.MusicVO
import java.util.*

class NeteaseMusic2 {
    var id = -1L
    var name = ""
    @SerializedName("ar")
    var artist: List<NeteaseMusic2Artist>? = null
    @SerializedName("al")
    var album: NeteaseMusic2Album? = null
    @SerializedName("dt")
    var duration = -1

    fun getMusicVO(): MusicVO {
        val vo = MusicVO(id, name)
        vo.artistNames = artist?.map { it.name } ?: listOf<String>()
        vo.albumName = album?.name ?: ""
        return vo
    }

    fun getMusic(): Music {
        val music = Music()
        music.musicId = id
        music.name = name
        music.path = ""
        music.album = getAlbum()
        music.albumId = album?.id
        music.albumName = album?.name
        music.artists = getArtists()
        music.artistIds = artist?.map { it.id }
        music.artistNames = artist?.map { it.name }
        music.duration = duration
        music.local = false
        music.download = false
        return music
    }

    fun getArtists(): List<Artist> {
        return artist?.map { it.getArtist() } ?: Collections.emptyList()
    }

    fun getAlbum(): Album? {
        return album?.getAlbum()
    }

    class NeteaseMusic2Artist {
        var id = -1L
        var name = ""
        var alias: List<String>? = null

        fun getArtist(): Artist {
            val artist = Artist()
            artist.artistId = id
            artist.name = name
            //artist.picUrl = picUrl
            artist.alias = alias
            return artist
        }
    }

    class NeteaseMusic2Album {
        var id = -1L
        var name = ""
        var picUrl = ""

        fun getAlbum(): Album {
            val album = Album()
            album.albumId = id
            album.name = name
            album.picUrl = picUrl
            return album
        }
    }
}