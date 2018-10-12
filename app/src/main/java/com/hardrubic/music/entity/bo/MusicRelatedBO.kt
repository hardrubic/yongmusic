package com.hardrubic.music.entity.bo

import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music

class MusicRelatedBO {
    var music: Music? = null
    var album: Album? = null
    var artists: List<Artist> = listOf()

}