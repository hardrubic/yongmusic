package com.hardrubic.music.biz.adapter

import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.aidl.MusicAidl
import com.hardrubic.music.entity.vo.MusicVO

object MusicEntityAdapter {
    fun toMusicAidl(music: Music): MusicAidl {
        return MusicAidl().apply {
            this.musicId = music.musicId
            this.name = music.name
            this.path = music.path
            this.duration = music.duration
            this.artistNames = music.artistNames.joinToString(separator = ",")
            this.albumName = music.albumName
            this.coverUrl = music.album.picUrl
        }
    }

    fun toMusicVO(music: Music): MusicVO {
        return MusicVO(music.musicId, music.name).apply {
            artistNames = music.artistNames
            albumName = music.albumName
        }
    }
}