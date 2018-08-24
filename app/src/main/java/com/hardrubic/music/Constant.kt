package com.hardrubic.music

class Constant {
    companion object {
        const val LOVE_COLLECTION_ID = 0L
        const val LOCAL_MUSIC_MINIMUM_SIZE = 1024 * 1024 //1M
        const val LOCAL_MUSIC_MINIMUM_SECOND = 60 * 1000 //1分钟
        const val APP_NAME = "yong"
    }

    object Param {
        const val CURRENT_MUSIC = "CURRENT_MUSIC"
        const val PLAY_MODEL = "PLAY_MODEL"
        const val PROGRESS = "PROGRESS"
        const val FLAG = "FLAG"
        const val LIST = "LIST"
        const val NAME = "NAME"
        const val COLLECTION_ID = "COLLECTION_ID"
        const val MUSIC_ID = "MUSIC_ID"
        const val ARTIST_ID = "ARTIST_ID"
        const val ALBUM_ID = "ALBUM_ID"
        const val NETWORK = "NETWORK"
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

    object SpKey {
        const val IS_LOGIN = "is_login"
        const val LOGIN_ID = "login_id"
        const val LOGIN_NAME = "login_name"
        const val PLAY_MODEL = "play_model"
        const val PLAY_LIST = "play_list"
        const val PLAYING = "playing"
    }

    object PlayModel {
        //列表循环
        const val LIST = 0
        //随机播放
        const val RANDOM = 1
        //单曲循环
        const val SINGLE = 2
    }

    object ItemType {
        const val MUSIC = 0
        const val ARTIST = 1
        const val ALBUM = 2
    }
}