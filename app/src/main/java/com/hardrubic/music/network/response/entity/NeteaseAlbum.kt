package com.hardrubic.music.network.response.entity

class NeteaseAlbum {
    var id: Long = -1
    var name = ""
    var picUrl = ""
    var artists: List<NeteaseArtist>? = null
    var publishTime = -1L
    var alias: List<String>? = null
}