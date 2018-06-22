package com.hardrubic.music.biz.repository

import com.hardrubic.music.db.DbManager
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.db.dataobject.MusicDao
import com.hardrubic.music.db.dataobject.RecentDao
import javax.inject.Inject

class MusicRepository @Inject constructor() {
    private val musicDao: MusicDao
        get() = DbManager.instance.session.musicDao

    private val recentDao: RecentDao
        get() = DbManager.instance.session.recentDao

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

    fun add(music: Music) {
        add(listOf(music))
    }

    fun add(musics: List<Music>) {
        musicDao.insertOrReplaceInTx(musics)
    }

    fun delete(id: Long) {
        musicDao.deleteByKey(id)
    }

    fun delete(ids: List<Long>) {
        musicDao.deleteByKeyInTx(ids)
    }
}