package com.hardrubic.music.di.component

import android.app.Application
import com.hardrubic.music.application.AppApplication
import com.hardrubic.music.di.module.ActivityBindModule
import com.hardrubic.music.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AndroidSupportInjectionModule::class, ActivityBindModule::class])
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(appApplication: AppApplication)
}