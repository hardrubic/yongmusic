package com.hardrubic.music.network

import com.google.gson.Gson
import com.hardrubic.music.application.AppApplication
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.bo.MusicResourceBO
import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.response.*
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
    fun applySearchMusic(searchText: String): Single<List<MusicVO>> {
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
                    response.getMusicVO()
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

    fun applyMusicResource(musicIds: List<Long>): Single<List<MusicResourceBO>> {
        val param = hashMapOf<String, String>()
        param["ids"] = "[${musicIds.joinToString(separator = ",")}]"
        param["br"] = 3200000.toString()

        return api.musicResource(param)
                .subscribeOn(Schedulers.io())
                .compose(CheckResponseCode<MusicResourceResponse>())
                .map {
                    it.getMusicResourceBOs()
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

    //todo 分页
    fun applyArtistHotAlbum(artistId: Long): Single<List<AlbumVO>> {
        val param = hashMapOf<String, String>()
        param["offset"] = "0"
        param["limit"] = "30"
        param["total"] = "true"
        //param["csrf_token"] = ""

        val encryptParam = JSUtil.buildEncryptParamMap(AppApplication.instance(), param)

        return api.artistHotAlbum(artistId, encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode<ArtistHotAlbumResponse>())
                .map {
                    it.getAlbumVOs()
                }
    }

    fun applyMusicDetail(musicIds: List<Long>): Single<List<Music>> {

        val ids = mutableListOf<MusicDetailParamId>()
        musicIds.forEach {
            ids.add(MusicDetailParamId(it))
        }

        val param = hashMapOf<String, String>()
        param["c"] = Gson().toJson(ids)

        val encryptParam = JSUtil.buildEncryptParamMap(AppApplication.instance(), param)

        return api.musicDetail(encryptParam)
                .subscribeOn(Schedulers.io())
                .compose(CheckResponseCode<MusicDetailResponse>())
                .map {
                    it.getMusics()
                }
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

    private class MusicDetailParamId(val id: Long)
}
