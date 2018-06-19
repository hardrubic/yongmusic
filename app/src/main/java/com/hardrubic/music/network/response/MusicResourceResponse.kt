package com.hardrubic.music.network.response

class MusicResourceResponse {
    lateinit var data: List<Result>
    var code: Int = 0

    class Result {
        var id: Long = 0
        var url = ""
        var size = 0
        var md5 = ""
    }
}

