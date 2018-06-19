package com.hardrubic.music.biz.adapter

import com.hardrubic.music.aidl.MusicAidl
import com.hardrubic.music.db.dataobject.Music

object MusicAidlAdapter {
    fun toMusicAidl(music: Music): MusicAidl {
        return MusicAidl().apply {
            this.musicId = music.musicId
            this.name = music.name
            this.path = music.path
            this.artist = music.artist
            this.duration = music.duration
            this.size = music.size
        }
    }

    fun toCommonMusic(musicAidl: MusicAidl): Music {
        return Music().apply {
            this.musicId = musicAidl.musicId
            this.name = musicAidl.name
            this.path = musicAidl.path
            this.artist = musicAidl.artist
            this.duration = musicAidl.duration
            this.size = musicAidl.size
        }
    }
}