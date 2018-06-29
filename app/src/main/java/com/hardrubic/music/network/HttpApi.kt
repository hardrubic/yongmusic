package com.hardrubic.music.network

import com.hardrubic.music.network.response.*
import io.reactivex.Single
import retrofit2.http.*

public interface HttpApi {

    @FormUrlEncoded
    @POST("/weapi/login/cellphone/")
    fun login(@FieldMap paramMap: Map<String, String>): Single<LoginResponse>

    @FormUrlEncoded
    @POST("/api/search/pc/")
    fun searchMusic(@FieldMap paramMap: Map<String, String>): Single<SearchMusicResponse>

    @FormUrlEncoded
    @POST("/api/search/pc/")
    fun searchArtist(@FieldMap paramMap: Map<String, String>): Single<SearchArtistResponse>

    @FormUrlEncoded
    @POST("/api/search/pc/")
    fun searchAlbum(@FieldMap paramMap: Map<String, String>): Single<SearchAlbumResponse>

    @GET("/api/song/enhance/player/url/")
    fun musicResource(@QueryMap paramMap: Map<String, String>): Single<MusicResourceResponse>

    @FormUrlEncoded
    @POST("/weapi/v1/artist/{id}")
    @Headers("Referer:https://music.163.com/")
    fun artistHotMusic(@Path("id") artistId: Long, @FieldMap paramMap: Map<String, String>): Single<ArtistHotMusicResponse>
}