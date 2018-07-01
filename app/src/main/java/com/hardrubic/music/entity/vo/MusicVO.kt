package com.hardrubic.music.entity.vo

import java.io.Serializable

class MusicVO(val musicId: Long, val name: String) : Serializable {
    var artistNames = listOf<String>()
    var albumName = ""
    var duration = -1
}