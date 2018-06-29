package com.hardrubic.music.network.response

import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.network.response.entity.NeteaseMusic
import java.util.*

class SearchMusicResponse : BaseResponse() {
    var result: Result? = null

    class Result {
        lateinit var songs: List<NeteaseMusic>
        var sonCount: Int = -1
    }

    fun getMusics(): List<Music> {
        return result?.songs?.map {
            it.getMusic()
        } ?: Collections.emptyList()
    }
}

