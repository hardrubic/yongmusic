package com.hardrubic.music.biz.adapter

import com.hardrubic.music.aidl.MusicAidl
import com.hardrubic.music.db.dataobject.Music

object MusicAidlAdapter {
    fun toMusicAidl(music: Music): MusicAidl {
        return MusicAidl().apply {
            this.musicId = music.musicId
            this.name = music.name
            this.path = music.path
            this.duration = music.duration
            this.size = music.size
        }
    }
}