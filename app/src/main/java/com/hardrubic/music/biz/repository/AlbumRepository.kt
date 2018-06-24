package com.hardrubic.music.biz.repository

import com.hardrubic.music.db.DbManager
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.AlbumDao
import com.hardrubic.music.db.dataobject.Artist
import javax.inject.Inject

class AlbumRepository @Inject constructor() {

    private val albumDao: AlbumDao
        get() = DbManager.instance.session.albumDao

    fun query(albumId: Long): Album? {
        return albumDao.load(albumId)
    }

    fun add(album: Album) {
        add(listOf(album))
    }

    fun add(albums: List<Album>) {
        if (albums.isNotEmpty()) {
            albumDao.insertOrReplaceInTx(albums)
        }
    }
}