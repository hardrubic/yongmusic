package com.hardrubic.music.network.response.entity

import com.google.gson.annotations.SerializedName
import com.hardrubic.music.entity.vo.MusicVO

class NeteaseMusic2 {
    var id = -1L
    var name = ""
    @SerializedName("ar")
    var artist: List<NeteaseMusic2Artist>? = null
    @SerializedName("al")
    var album: NeteaseMusic2Album? = null

    fun getMusicVO(): MusicVO {
        val vo = MusicVO(id, name)
        vo.artistNames = artist?.map { it.name } ?: listOf<String>()
        vo.albumName = album?.name ?: ""
        return vo
    }

    class NeteaseMusic2Artist {
        var id = -1L
        var name = ""
    }

    class NeteaseMusic2Album {
        var id = -1L
        var name = ""
        var picUrl = ""
    }
}