package com.hardrubic.music.network.response

import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.response.entity.NeteaseMusic
import java.util.*

class SearchMusicResponse : BaseResponse() {
    var result: Result? = null

    class Result {
        lateinit var songs: List<NeteaseMusic>
        var sonCount: Int = -1
    }

    fun getMusicVO(): List<MusicVO> {
        return result?.songs?.map {
            it.getMusicVO()
        } ?: Collections.emptyList()
    }
}

