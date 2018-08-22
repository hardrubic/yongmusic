package com.hardrubic.music.biz.player

import com.hardrubic.music.Constant
import com.hardrubic.music.entity.aidl.MusicAidl
import java.util.*

class MusicDispatch {
    var musics = listOf<MusicAidl>()
    var playModel = Constant.PlayModel.LIST
    var playingMusic: MusicAidl? = null
    private var nextMusic: MusicAidl? = null

    //todo 随机模式上一首，需要记录播放路径
    fun previous(): MusicAidl? {
        if (musics.isEmpty()) {
            return null
        }

        if (playingMusic == null) {
            return musics.first()
        }

        val index = musics.indexOf(playingMusic!!)
        return when {
            index == 0 -> musics[musics.lastIndex]
            index < 0 -> musics.first()
            else -> musics[index - 1]
        }
    }

    fun next(): MusicAidl? {
        this.nextMusic = when (playModel) {
            Constant.PlayModel.RANDOM -> randomPlayModelGetNext()
            else -> listPlayModelGetNext()
        }
        return nextMusic
    }

    private fun randomPlayModelGetNext(): MusicAidl? {
        if (musics.isEmpty()) {
            return null
        }

        if (playingMusic != null) {
            val playingIndex = musics.indexOf(playingMusic!!)
            var nextIndex = playingIndex
            while (playingIndex == nextIndex) {
                nextIndex = Random().nextInt(musics.size)
            }
            return musics[nextIndex]
        }

        return musics[Random().nextInt(musics.size)]
    }

    private fun listPlayModelGetNext(): MusicAidl? {
        return when {
            musics.isEmpty() -> null
            playingMusic != null -> {
                val index = musics.indexOf(playingMusic!!)
                when {
                    index < 0 -> musics.first()
                    index == musics.lastIndex -> musics.first()
                    else -> musics[index + 1]
                }
            }
            else -> {
                musics.first()
            }
        }
    }

    fun playNext(music: MusicAidl) {
        assert(musics.contains(music))
        this.playingMusic = music
    }

    fun playNext(): MusicAidl? {
        this.playingMusic = nextMusic
        this.nextMusic = null
        return playingMusic
    }

    fun singlePlaying(): Boolean {
        return playModel == Constant.PlayModel.SINGLE
    }
}