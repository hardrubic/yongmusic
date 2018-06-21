package com.hardrubic.music

class Constant {
    companion object {
        val LOVE_COLLECTION_ID = 0L
    }

    object Param {
        const val CURRENT_MUSIC = "CURRENT_MUSIC"
        const val PLAY_MODEL = "PLAY_MODEL"
        const val PROGRESS = "PROGRESS"
        const val FLAG = "FLAG"
        const val LIST = "LIST"
        const val COLLECTION_ID = "COLLECTION_ID"
        const val MUSIC_ID = "MUSIC_ID"
    }

    object RequestCode {
        const val SELECT_COLLECTION = 100
    }

    object ResultCode {
    }

    object BroadcastAction {
        const val PROGRESS = "com.hardrubic.music.PROGRESS"
        const val CURRENT_MUSIC = "com.hardrubic.music.CURRENT_MUSIC"
        const val PLAY_STATE = "com.hardrubic.music.PLAY_STATE"
    }

    object PlayModel {
        //列表循环
        const val LIST = 0
        //随机播放
        const val RANDOM = 1
        //单曲循环
        const val SINGLE = 2
    }

    object SpKey {
        const val PLAY_MODEL = "play_model"
        const val PLAY_LIST = "play_list"
        const val PLAYING = "playing"
    }
}