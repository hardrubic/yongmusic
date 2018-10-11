package com.hardrubic.music.biz.repository

import com.hardrubic.music.db.DbManager
import com.hardrubic.music.db.dataobject.*
import com.hardrubic.music.entity.bo.MusicRelatedBO
import javax.inject.Inject

class MusicRepository @Inject constructor() {
    private val musicDao: MusicDao
        get() = DbManager.instance.session.musicDao

    private val albumDao: AlbumDao
        get() = DbManager.instance.session.albumDao

    private val artistDao: ArtistDao
        get() = DbManager.instance.session.artistDao

    private val recentDao: RecentDao
        get() = DbManager.instance.session.recentDao

    fun saveMusicRelated(relatedBO: List<MusicRelatedBO>) {
        val artistSet = mutableSetOf<Artist>()
        relatedBO.map { it.artists }.forEach {
            artistSet.addAll(it)
        }

        addMusic(relatedBO.map { it.music })
        addAlbum(relatedBO.mapNotNull { it.album })
        addArtist(artistSet.toList())
    }

    fun queryMusic(id: Long): Music? {
        return musicDao.load(id)
    }

    fun queryMusic(ids: List<Long>): List<Music> {
        val queryBuilder = musicDao.queryBuilder()
        queryBuilder.where(MusicDao.Properties.MusicId.`in`(ids))
        return queryBuilder.list()
    }

    fun queryLocalMusic(): List<Music> {
        val queryBuilder = musicDao.queryBuilder()
        queryBuilder.where(MusicDao.Properties.Local.eq(true))
        return queryBuilder.list()
    }

    fun addMusic(music: Music) {
        addMusic(listOf(music))
    }

    fun addMusic(musics: List<Music>) {
        musicDao.insertOrReplaceInTx(musics)
    }

    fun deleteMusic(id: Long) {
        musicDao.deleteByKey(id)
    }

    fun deleteMusic(ids: List<Long>) {
        musicDao.deleteByKeyInTx(ids)
    }

    fun queryAlbum(albumId: Long): Album? {
        return albumDao.load(albumId)
    }

    fun addAlbum(album: Album) {
        addAlbum(listOf(album))
    }

    fun addAlbum(albums: List<Album>) {
        if (albums.isNotEmpty()) {
            albumDao.insertOrReplaceInTx(albums)
        }
    }

    fun queryArtist(artistId: Long): Artist? {
        return artistDao.load(artistId)
    }

    fun addArtist(artist: Artist) {
        addArtist(listOf(artist))
    }

    fun addArtist(artists: List<Artist>) {
        if (artists.isNotEmpty()) {
            artistDao.insertOrReplaceInTx(artists)
        }
    }
}