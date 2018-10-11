package com.hardrubic.music.entity.bo

class MusicResourceBO(val musicId: Long) {
    lateinit var dir: String
    lateinit var saveName: String
    var md5: String? = null
    var url: String? = null

    public fun getPath(): String {
        return dir + saveName
    }
}