package com.hardrubic.music.network.response.entity

class NeteaseAlbumDetail {
    var name = ""
    var id: Long = -1
    var picUrl = ""
    var briefDesc = ""
    var publishTime = -1L
    var company = ""
    var artists: List<NeteaseArtist>? = null
    var description = ""

    fun getArtistNames(): List<String> {
        return artists?.map { it.name } ?: listOf()
    }
}