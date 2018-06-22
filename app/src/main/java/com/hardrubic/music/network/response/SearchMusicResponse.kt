package com.hardrubic.music.network.response

import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.network.response.entity.NeteaseAlbum
import com.hardrubic.music.network.response.entity.NeteaseArtist
import java.util.*

class SearchMusicResponse : BaseResponse() {
    var result: Result? = null

    class Result {
        lateinit var songs: List<NeteaseMusic>
        var sonCount: Int = -1
    }

    class NeteaseMusic {
        var id: Long = -1
        var name = ""
        var artists: List<NeteaseArtist>? = null
        var album: NeteaseAlbum? = null
        var duration = 0
    }

    fun getArtists(neteaseMusic: NeteaseMusic): List<Artist> {
        return neteaseMusic.artists?.map {
            it.getArtist()
        } ?: Collections.emptyList()
    }

    fun getMusics(): List<Music> {
        return result?.songs?.map {
            val music = Music()
            music.musicId = it.id
            music.name = it.name
            music.duration = it.duration
            music.artistIds = it.artists?.map { it.id }
            music.artistNames = it.artists?.map { it.name }
            music.artists = getArtists(it)
            music.path = ""
            music.download = false
            music.local = false
            music
        } ?: Collections.emptyList()
    }
}

