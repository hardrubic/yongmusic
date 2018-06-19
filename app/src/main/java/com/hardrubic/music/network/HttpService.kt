package com.hardrubic.music.network

import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.network.response.MusicResourceResponse
import com.hardrubic.music.network.response.SearchMusicResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URLEncoder
import kotlin.properties.Delegates


class HttpService {

    fun applySearchMusic(searchText: String): Single<List<Music>> {
        val param = hashMapOf<String, String>()
        param["s"] = searchText
        param["type"] = 1.toString()
        param["limit"] = 10.toString()
        param["offset"] = 0.toString()

        return api.searchMusic(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { response: SearchMusicResponse ->
                    response.result.songs.map {
                        val music = Music()
                        music.musicId = it.id
                        music.name = it.name
                        music.artist = it.artists?.first()?.name ?: ""
                        music.duration = it.duration
                        music.path = ""
                        music.download = false
                        music.local = false
                        music
                    }
                }
    }

    fun applyMusicResource(musicId: Long): Single<Pair<String, String>> {
        val param = hashMapOf<String, String>()
        param["id"] = musicId.toString()
        param["ids"] = "[$musicId]"
        param["br"] = 3200000.toString()

        return api.musicResource(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { response: MusicResourceResponse ->
                    val result = response.data.first()
                    Pair(result.url, result.md5)
                }

    }

    companion object {

        private lateinit var api: HttpApi
        var instance: HttpService by Delegates.notNull()

        fun register() {
            instance = HttpService()
            api = HttpManager().createApi(HttpApi::class.java)
        }
    }


}
