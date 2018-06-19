package com.hardrubic.music.db

import android.content.Context
import com.hardrubic.music.db.dataobject.DaoMaster
import org.greenrobot.greendao.query.QueryBuilder
import kotlin.properties.Delegates

class DbManager {
    companion object {
        lateinit var context: Context
        var instance: DbManager by Delegates.notNull()

        private val DB_NAME = "abc_music_db"

        fun init(context: Context) {
            this.context = context
            this.instance = DbManager()
        }
    }

    val database by lazy {
        val dbUpgrade = DbUpgrade(context, DB_NAME, null)
        dbUpgrade.readableDatabase
    }

    val session by lazy {
        val daoMaster = DaoMaster(database)
        daoMaster.newSession()
    }

    /**
     * 设置是否打印原生sql语句
     */
    fun setDebug(isDebug: Boolean) {
        QueryBuilder.LOG_SQL = isDebug
        QueryBuilder.LOG_VALUES = isDebug
    }

}