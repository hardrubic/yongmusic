package com.hardrubic.music.network.response

import com.hardrubic.music.entity.bo.MusicResourceBO

class MusicResourceResponse : BaseResponse() {
    var data: List<Result>? = null

    class Result {
        var id: Long = 0
        var url = ""
        var size = 0
        var md5 = ""
    }

    fun getMusicResourceBOs(): List<MusicResourceBO> {
        return data?.map {
            val bo = MusicResourceBO(it.id)
            bo.md5 = it.md5
            bo.url = it.url
            bo
        } ?: listOf()
    }
}

