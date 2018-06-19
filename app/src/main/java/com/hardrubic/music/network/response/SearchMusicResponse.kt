package com.hardrubic.music.network.response

import com.google.gson.annotations.SerializedName

class SearchMusicResponse {
    lateinit var result: Result
    var code: Int = 0

    class Result {
        lateinit var songs: List<NeteaseMusic>
        var sonCount: Int = -1
    }

    class NeteaseMusic {
        var id: Long = -1
        var name = ""
        var artists: List<NeteaseArtists>? = null
        var duration = 0
        var mp3Url = ""
    }

    class NeteaseArtists {
        var name = ""
        @SerializedName("id")
        var artistId: Int = -1
    }
}

