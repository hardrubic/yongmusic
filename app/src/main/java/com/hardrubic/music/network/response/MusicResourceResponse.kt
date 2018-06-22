package com.hardrubic.music.network.response

class MusicResourceResponse : BaseResponse() {
    var data: List<Result>? = null

    class Result {
        var id: Long = 0
        var url = ""
        var size = 0
        var md5 = ""
    }
}

