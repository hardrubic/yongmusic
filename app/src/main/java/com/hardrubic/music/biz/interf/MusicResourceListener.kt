package com.hardrubic.music.biz.interf

import com.hardrubic.music.db.dataobject.Music

interface MusicResourceListener {
    fun onProgress(progress: Int, max: Int)

    fun onSuccess(musics: List<Music>)

    fun onError(e: Throwable)
}