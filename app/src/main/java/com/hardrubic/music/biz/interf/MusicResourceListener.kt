package com.hardrubic.music.biz.interf

import com.hardrubic.music.entity.bo.MusicRelatedBO

interface MusicResourceListener {
    fun onProgress(progress: Int, max: Int)

    fun onSuccess(musicRelatedBOS: List<MusicRelatedBO>)

    fun onError(e: Throwable)
}