package com.hardrubic.music.network

import com.hardrubic.music.network.response.*
import io.reactivex.Single
import retrofit2.http.*

public interface HttpApi {

    @FormUrlEncoded
    @POST("/weapi/login/cellphone/")
    fun login(@FieldMap paramMap: Map<String, String>): Single<LoginResponse>

    @FormUrlEncoded
    @POST("/weapi/search/get")
    //@POST("/api/search/pc/")
    fun searchMusic(@FieldMap paramMap: Map<String, String>): Single<SearchMusicResponse>

    @FormUrlEncoded
    @POST("/weapi/search/get")
    fun searchArtist(@FieldMap paramMap: Map<String, String>): Single<SearchArtistResponse>

    @FormUrlEncoded
    @POST("/weapi/search/get")
    fun searchAlbum(@FieldMap paramMap: Map<String, String>): Single<SearchAlbumResponse>

    @GET("/api/song/enhance/player/url/")
    fun musicResource(@QueryMap paramMap: Map<String, String>): Single<MusicResourceResponse>

    @FormUrlEncoded
    @POST("/weapi/v1/artist/{id}")
    @Headers("Referer:https://music.163.com/")
    fun artistHotMusic(@Path("id") artistId: Long, @FieldMap paramMap: Map<String, String>): Single<ArtistHotMusicResponse>

    @FormUrlEncoded
    @POST("/weapi/artist/albums/{id}")
    @Headers("Referer:https://music.163.com/")
    fun artistHotAlbum(@Path("id") artistId: Long, @FieldMap paramMap: Map<String, String>): Single<ArtistHotAlbumResponse>

    @FormUrlEncoded
    @POST("/weapi/v3/song/detail")
    @Headers("Referer:https://music.163.com/")
    fun musicDetail(@FieldMap paramMap: Map<String, String>): Single<MusicDetailResponse>

    @FormUrlEncoded
    @POST("/weapi/v1/album/{id}")
    @Headers("Referer:https://music.163.com/")
    fun albumDetail(@Path("id") albumId: Long, @FieldMap paramMap: Map<String, String>): Single<AlbumDetailResponse>

}