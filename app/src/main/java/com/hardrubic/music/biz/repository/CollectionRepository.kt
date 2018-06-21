package com.hardrubic.music.biz.repository

import com.hardrubic.music.db.DbManager
import com.hardrubic.music.db.dataobject.Collection
import com.hardrubic.music.db.dataobject.CollectionDao
import com.hardrubic.music.db.dataobject.MusicCollectionRelation
import com.hardrubic.music.db.dataobject.MusicCollectionRelationDao
import javax.inject.Inject

class CollectionRepository @Inject constructor() {
    private val collectionDao: CollectionDao
        get() = DbManager.instance.session.collectionDao

    private val musicCollectionRelationDao: MusicCollectionRelationDao
        get() = DbManager.instance.session.musicCollectionRelationDao

    fun queryAllCollection(): List<Collection> {
        return collectionDao.loadAll()
    }

    fun queryCollection(id: Long): Collection? {
        return collectionDao.load(id)
    }

    fun queryCollectionMusicIds(collectionId: Long): List<Long> {
        val queryBuilder = musicCollectionRelationDao.queryBuilder()
        queryBuilder.where(MusicCollectionRelationDao.Properties.CollectionId.eq(collectionId))
        return queryBuilder.list().map { it.musicId }
    }

    fun addCollection(name: String, id: Long? = null): Long {
        val collection = Collection()

        if (id != null) {
            collection.id = id
        }

        collection.name = name
        collection.resId = null
        collection.createdAt = System.currentTimeMillis()

        return collectionDao.insert(collection)
    }

    fun addMusicToCollection(musicId: Long, collectionId: Long) {
        val relation = MusicCollectionRelation()
        relation.collectionId = collectionId
        relation.musicId = musicId

        musicCollectionRelationDao.insert(relation)
    }

    fun deleteMusicFromCollection(musicId: Long, collectionId: Long) {
        val queryBuilder = musicCollectionRelationDao.queryBuilder()
        queryBuilder.where(MusicCollectionRelationDao.Properties.CollectionId.eq(collectionId))
        queryBuilder.where(MusicCollectionRelationDao.Properties.MusicId.eq(musicId))
        queryBuilder.buildDelete().executeDeleteWithoutDetachingEntities()
    }

    fun deleteCollection(collectionId: Long) {
        val queryBuilder = musicCollectionRelationDao.queryBuilder()
        queryBuilder.where(MusicCollectionRelationDao.Properties.CollectionId.eq(collectionId))
        queryBuilder.buildDelete().executeDeleteWithoutDetachingEntities()

        collectionDao.deleteByKey(collectionId)
    }

    fun checkMusicInCollection(musicId: Long, collectionId: Long): Boolean {
        val queryBuilder = musicCollectionRelationDao.queryBuilder()
        queryBuilder.where(MusicCollectionRelationDao.Properties.CollectionId.eq(collectionId))
        queryBuilder.where(MusicCollectionRelationDao.Properties.MusicId.eq(musicId))
        return queryBuilder.buildCount().count() > 0
    }
}