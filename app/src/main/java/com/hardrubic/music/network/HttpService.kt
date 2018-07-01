package com.hardrubic.music.network

import com.hardrubic.music.application.AppApplication
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.network.response.*
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import com.hardrubic.music.util.JSUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates


class HttpService {

    fun applyLogin(phone: String, password: String): Single<LoginResponse> {
        val param = hashMapOf<String, String>()
        param["phone"] = phone
        param["password"] = password
        param["rememberLogin"] = "true"

        val encryptParam = JSUtil.buildEncryptParamMap(AppApplication.instance(), param)

        return api.login(encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode())
    }

    //todo 分页
    fun applySearchMusic(searchText: String): Single<List<Music>> {
        val param = hashMapOf<String, String>()
        param["s"] = searchText
        param["type"] = 1.toString()
        param["limit"] = 10.toString()
        param["offset"] = 0.toString()

        return api.searchMusic(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode<SearchMusicResponse>())
                .map { response: SearchMusicResponse ->
                    response.getMusics()
                }
    }

    fun applySearchArtist(searchText: String): Single<List<Artist>> {
        val param = hashMapOf<String, String>()
        param["s"] = searchText
        param["type"] = 100.toString()
        param["limit"] = 10.toString()
        param["offset"] = 0.toString()

        return api.searchArtist(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode<SearchArtistResponse>())
                .map { response: SearchArtistResponse ->
                    response.getArtists()
                }
    }

    fun applySearchAlbum(searchText: String): Single<List<Album>> {
        val param = hashMapOf<String, String>()
        param["s"] = searchText
        param["type"] = 10.toString()
        param["limit"] = 10.toString()
        param["offset"] = 0.toString()

        return api.searchAlbum(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode<SearchAlbumResponse>())
                .map { response: SearchAlbumResponse ->
                    response.getAlbums()
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
                .compose(CheckResponseCode<MusicResourceResponse>())
                .map { response: MusicResourceResponse ->
                    val result = response.data?.first()
                    Pair(result?.url ?: "", result?.md5 ?: "")
                }
    }

    fun applyArtistHotMusic(artistId: Long): Single<ArtistHotMusicResponse> {
        val param = hashMapOf<String, String>()
        param["csrf_token"] = ""

        val encryptParam = JSUtil.buildEncryptParamMap(AppApplication.instance(), param)

        return api.artistHotMusic(artistId, encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode())
    }

    fun applyAlbumDetail(albumId: Long): Single<AlbumDetailResponse> {
        val param = hashMapOf<String, String>()
        param["csrf_token"] = ""

        val encryptParam = JSUtil.buildEncryptParamMap(AppApplication.instance(), param)

        return api.albumDetail(albumId, encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode())
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
