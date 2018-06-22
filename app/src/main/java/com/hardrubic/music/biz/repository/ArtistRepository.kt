package com.hardrubic.music.biz.repository

import com.hardrubic.music.db.DbManager
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.ArtistDao
import javax.inject.Inject

class ArtistRepository @Inject constructor() {

    private val artistDao: ArtistDao
        get() = DbManager.instance.session.artistDao

    fun query(artistId: Long): Artist? {
        return artistDao.load(artistId)
    }

    fun add(artist: Artist) {
        add(listOf(artist))
    }

    fun add(artists: List<Artist>) {
        if (artists.isNotEmpty()) {
            artistDao.insertOrReplaceInTx(artists)
        }
    }
}