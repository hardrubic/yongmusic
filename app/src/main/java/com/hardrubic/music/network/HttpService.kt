package com.hardrubic.music.network

import com.google.gson.Gson
import com.hardrubic.music.biz.encrypt.EncryptParamBuilder
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.entity.MusicDetailParamId
import com.hardrubic.music.entity.bo.MusicRelatedBO
import com.hardrubic.music.entity.bo.MusicResourceBO
import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.response.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class HttpService @Inject constructor(val api: HttpApi) {

    fun applyLogin(phone: String, password: String): Single<LoginResponse> {
        val param = hashMapOf<String, String>()
        param["phone"] = phone
        param["password"] = password
        param["rememberLogin"] = "true"

        val encryptParam = EncryptParamBuilder.encrypt(param)

        return api.login(encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode())
    }

    fun applySearchMusic(searchText: String, limit: Int, offset: Int): Single<List<MusicVO>> {
        val param = hashMapOf<String, String>()
        param["s"] = searchText
        param["type"] = 1.toString()
        param["limit"] = "$limit"
        param["offset"] = "$offset"

        val encryptParam = EncryptParamBuilder.encrypt(param)

        return api.searchMusic(encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode<SearchMusicResponse>())
                .map { response: SearchMusicResponse ->
                    response.getMusicVO()
                }
    }

    fun applySearchArtist(searchText: String, limit: Int, offset: Int): Single<List<Artist>> {
        val param = hashMapOf<String, String>()
        param["s"] = searchText
        param["type"] = 100.toString()
        param["limit"] = "$limit"
        param["offset"] = "$offset"

        val encryptParam = EncryptParamBuilder.encrypt(param)

        return api.searchArtist(encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode<SearchArtistResponse>())
                .map { response: SearchArtistResponse ->
                    response.getArtists()
                }
    }

    fun applySearchAlbum(searchText: String, limit: Int, offset: Int): Single<List<AlbumVO>> {
        val param = hashMapOf<String, String>()
        param["s"] = searchText
        param["type"] = 10.toString()
        param["limit"] = "$limit"
        param["offset"] = "$offset"

        val encryptParam = EncryptParamBuilder.encrypt(param)

        return api.searchAlbum(encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode<SearchAlbumResponse>())
                .map { response: SearchAlbumResponse ->
                    response.getAlbumVOs()
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

        val encryptParam = EncryptParamBuilder.encrypt(param)

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

        val encryptParam = EncryptParamBuilder.encrypt(param)

        return api.artistHotAlbum(artistId, encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode<ArtistHotAlbumResponse>())
                .map {
                    it.getAlbumVOs()
                }
    }

    //TODO 支持批量吗
    fun applyMusicDetail(musicId: Long, scheduler: Scheduler): Single<MusicRelatedBO> {
        val ids = mutableListOf<MusicDetailParamId>()
        ids.add(MusicDetailParamId(musicId))

        val param = hashMapOf<String, String>()
        param["c"] = Gson().toJson(ids)

        val encryptParam = EncryptParamBuilder.encrypt(param)

        return api.musicDetail(encryptParam)
                .subscribeOn(scheduler)
                .compose(CheckResponseCode<MusicDetailResponse>())
                .map { it.getMusicRelated() }
    }

    fun applyAlbumDetail(albumId: Long): Single<AlbumDetailResponse> {
        val param = hashMapOf<String, String>()
        param["csrf_token"] = ""

        val encryptParam = EncryptParamBuilder.encrypt(param)

        return api.albumDetail(albumId, encryptParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CheckResponseCode())
    }

}
