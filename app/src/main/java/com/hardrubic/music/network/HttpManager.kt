package com.hardrubic.music.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.hardrubic.music.BuildConfig
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class HttpManager {

    companion object {
        fun buildOkHttpClient(cookieManager: CookieJar): OkHttpClient {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            } else {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            }

            return OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(StethoInterceptor())
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .cookieJar(cookieManager)
                    .build()
        }

        fun buildApi(okHttpClient: OkHttpClient): HttpApi {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://music.163.com")
                    .addConverterFactory(GsonConverterFactory.create(Gson()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
            return retrofit.create(HttpApi::class.java)
        }

        fun buildCookieManager(): CookieJar {
            return object : CookieJar {
                //TODO 持久化
                private val cookiesMap = hashMapOf<String, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, inputCookies: MutableList<Cookie>) {
                    val host = url.host()
                    val cookies = cookiesMap[host]
                    if (cookies != null && cookies.isNotEmpty()) {
                        cookiesMap.remove(host)
                    }
                    cookiesMap[host] = inputCookies

                    //LogUtil.d("http cookie:$host -> $inputCookies")
                }

                override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                    val cookies = cookiesMap[url.host()]?.toMutableList() ?: Collections.emptyList()
                    //LogUtil.d("http load cookie:$cookies")
                    return cookies
                }
            }
        }
    }
}