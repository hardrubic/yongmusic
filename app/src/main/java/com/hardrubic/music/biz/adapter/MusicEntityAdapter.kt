package com.hardrubic.music.biz.adapter

import com.hardrubic.music.aidl.MusicAidl
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.vo.MusicVO

object MusicEntityAdapter {
    fun toMusicAidl(music: Music): MusicAidl {
        return MusicAidl().apply {
            this.musicId = music.musicId
            this.name = music.name
            this.path = music.path
            this.duration = music.duration
        }
    }

    fun toMusicVO(music: Music): MusicVO {
        return MusicVO(music.musicId, music.name).apply {
            artistNames = music.artists?.map { it.name } ?: listOf()
            albumName = music.album?.name ?: ""
        }
    }
}