package com.hardrubic.music.network.response

import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.network.response.entity.NeteaseArtist
import java.util.*

class SearchArtistResponse : BaseResponse() {
    var result: Result? = null

    class Result {
        var artists: List<NeteaseArtist>? = null
        var artistCount: Int = -1
    }

    fun getArtists(): List<Artist> {
        return result?.artists?.map {
            it.getArtist()
        } ?: Collections.emptyList()
    }
}

