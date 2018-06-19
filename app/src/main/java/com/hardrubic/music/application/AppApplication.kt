package com.hardrubic.music.application

import android.app.Application
import com.facebook.stetho.Stetho
import com.hardrubic.music.BuildConfig
import com.hardrubic.music.db.DbManager
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.util.PreferencesUtil
import zlc.season.rxdownload3.core.DownloadConfig

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        PreferencesUtil.initializeInstance(this)
        DbManager.init(this)
        HttpService.register()

        val builder = DownloadConfig.Builder.create(this).apply {
            this.enableDb(false)
            this.enableAutoStart(false)
            this.enableNotification(false)
            this.enableService(false)
        }
        DownloadConfig.init(builder)
    }

    companion object {
        private var instance: Application? = null

        fun instance(): Application = instance!!
    }
}