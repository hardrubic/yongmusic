package com.hardrubic.music.network

import android.text.TextUtils
import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class HttpManager {

    /**
     * 拦截的HTTP的日志信息
     */
    class LoggingInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val t1 = System.currentTimeMillis()
            Log.v(TAG, "http request -> ${request.url()} ${request.method()}")

            val response = chain.proceed(request)

            val t2 = System.currentTimeMillis()
            val time = (t2 - t1) / 1000f
            val msg = "http response -> ${response.request().url()} ${response.request().method()}, ${time}s"
            if (time > 1000) {
                Log.w(TAG, msg)
            } else {
                Log.d(TAG, msg)
            }
            val postParam = HttpUtil.buildHttpPostParam(request)
            if (!TextUtils.isEmpty(postParam)) {
                Log.d(TAG, "post param: -> $postParam")
            }

            return response
        }
    }

    companion object {
        private val TAG = "http"

        fun buildOkHttpClient(cookieManager: CookieJar): OkHttpClient {
            return OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .addInterceptor(HttpManager.LoggingInterceptor())
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

        fun buildCookieManager():CookieJar{
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