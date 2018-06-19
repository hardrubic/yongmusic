package com.hardrubic.music.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.hardrubic.music.db.dataobject.DaoMaster
import com.hardrubic.music.util.LogUtil
import org.greenrobot.greendao.database.Database


class DbUpgrade(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?)
    : DaoMaster.OpenHelper(context, name, factory) {

    override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
        if (oldVersion == newVersion) {
            LogUtil.d("数据库是最新版本$oldVersion，不需要升级")
            return
        }

        LogUtil.d("数据库从版本" + oldVersion + "升级到版本" + newVersion)

        when (oldVersion) {
            1 -> {
            }
            else -> {
            }
        }
    }
}
