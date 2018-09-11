package com.hardrubic.music.di.module

import com.hardrubic.music.biz.resource.MusicResourceDownload
import com.hardrubic.music.network.HttpApi
import com.hardrubic.music.network.HttpManager
import com.hardrubic.music.network.HttpService
import dagger.Module
import dagger.Provides
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(cookieManager: CookieJar): OkHttpClient {
        return HttpManager.buildOkHttpClient(cookieManager)
    }

    @Provides
    @Singleton
    fun provideApi(okHttpClient: OkHttpClient): HttpApi {
        return HttpManager.buildApi(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideCookieManager(): CookieJar {
        return HttpManager.buildCookieManager()
    }

    @Provides
    @Singleton
    fun provideHttpService(api: HttpApi): HttpService {
        return HttpService(api)
    }

    @Provides
    @Singleton
    fun provideMusicResourceDownload(httpService: HttpService): MusicResourceDownload {
        //TODO 单独放一个module
        return MusicResourceDownload(httpService)
    }
}