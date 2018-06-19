package com.hardrubic.music.biz.player

import com.hardrubic.music.Constant
import com.hardrubic.music.aidl.MusicAidl
import com.hardrubic.music.util.LogUtil
import java.util.*

class MusicDispatch {
    private val musics = mutableListOf<MusicAidl>()
    private var playingMusic: MusicAidl? = null
    private var playModel = Constant.PlayModel.LIST

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
        return when (playModel) {
            Constant.PlayModel.RANDOM -> randomPlayModelGetNext()
            else -> listPlayModelGetNext()
        }
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

    fun playing(music: MusicAidl) {
        assert(musics.contains(music))
        LogUtil.d("playing ${music.name}")
        this.playingMusic = music
    }

    fun singlePlaying(): Boolean {
        return playModel == Constant.PlayModel.SINGLE
    }

    fun updateMusics(list: List<MusicAidl>) {
        LogUtil.d("play list size ${list.size}")
        this.musics.clear()
        this.musics.addAll(list)
    }

    fun updatePlayModel(model: Int) {
        this.playModel = model
    }
}