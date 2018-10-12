package com.hardrubic.music.application

import android.app.Activity
import android.app.Application
import android.os.StrictMode
import com.facebook.stetho.Stetho
import com.hardrubic.music.BuildConfig
import com.hardrubic.music.db.DbManager
import com.hardrubic.music.di.component.DaggerAppComponent
import com.hardrubic.music.util.PreferencesUtil
import com.pgyersdk.crash.PgyCrashManager
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import zlc.season.rxdownload3.core.DownloadConfig
import javax.inject.Inject

class AppApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)

            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())

            LeakCanary.install(this)
        }

        PgyCrashManager.register()

        PreferencesUtil.initializeInstance(this)
        DbManager.init(this)

        val builder = DownloadConfig.Builder.create(this).apply {
            this.enableDb(false)
            this.enableAutoStart(false)
            this.enableNotification(false)
            this.enableService(false)
        }
        DownloadConfig.init(builder)

        initInjector()
    }

    private fun initInjector() {
        DaggerAppComponent.builder().application(this).build().inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }

    companion object {
        private var instance: Application? = null

        fun instance(): Application = instance!!
    }
}