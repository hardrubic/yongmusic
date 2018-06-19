package com.hardrubic.music.biz.repository

import com.hardrubic.music.db.DbManager
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.db.dataobject.Recent
import com.hardrubic.music.db.dataobject.RecentDao
import javax.inject.Inject

class RecentRepository @Inject constructor() {

    private val recentDao: RecentDao
        get() = DbManager.instance.session.recentDao

    fun query(musicId: Long): Recent? {
        return recentDao.load(musicId)
    }

    fun queryRecent(): Music? {
        val recents = queryRecentMusics()
        if (recents.isNotEmpty()) {
            return recents.first()
        }
        return null
    }

    fun queryRecentMusics(): List<Music> {
        val queryBuilder = recentDao.queryBuilder()
        queryBuilder.orderDesc(RecentDao.Properties.LastTime)
        val recents = queryBuilder.list()
        return recents.map { it.music }
    }

    fun add(musicId: Long) {
        var recent = query(musicId)
        if (recent == null) {
            recent = Recent().apply {
                this.musicId = musicId
                this.count = 0
            }
        } else {
            recent.count = recent.count + 1
        }
        recent.lastTime = System.currentTimeMillis()
        recentDao.insertOrReplace(recent)
    }

}