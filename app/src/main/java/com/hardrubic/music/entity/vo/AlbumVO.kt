package com.hardrubic.music.entity.vo

import java.io.Serializable

class AlbumVO(val albumId: Long, val name: String) : Serializable {
    var artistNames = listOf<String>()
    var alias = listOf<String>()
    var publishTime = -1L
    var picUrl: String = ""
}