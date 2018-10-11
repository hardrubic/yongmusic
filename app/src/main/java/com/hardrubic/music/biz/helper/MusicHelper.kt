package com.hardrubic.music.biz.helper

import com.hardrubic.music.db.dataobject.Music

object MusicHelper{
    fun sortMusicByTargetId(musics: List<Music>, targetMusicIds: List<Long>): List<Music> {
        val result = mutableListOf<Music>()
        for (id in targetMusicIds) {
            val target = musics.find { it.musicId == id } ?: continue
            result.add(target)
        }
        return result
    }
}