package com.hardrubic.music.biz.helper

import com.hardrubic.music.db.dataobject.Music

object MusicHelper{
    fun sortMusicByInitialId(musics: List<Music>, initialMusicIds: List<Long>): List<Music> {
        val result = mutableListOf<Music>()
        for (id in initialMusicIds) {
            val target = musics.find { it.musicId == id } ?: continue
            result.add(target)
        }
        return result
    }
}