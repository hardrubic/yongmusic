package com.hardrubic.music.network.response

import com.hardrubic.music.entity.bo.MusicRelatedBO
import com.hardrubic.music.network.response.entity.NeteaseMusic2

class MusicDetailResponse : BaseResponse() {
    var songs: List<NeteaseMusic2>? = null

    fun getMusicRelated(): MusicRelatedBO {
        //获取失败返回空BO
        return songs?.first()?.getMusic() ?: MusicRelatedBO()
    }
}

