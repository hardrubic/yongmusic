package com.hardrubic.music.network

import com.hardrubic.music.network.response.MusicResourceResponse
import com.hardrubic.music.network.response.SearchMusicResponse
import io.reactivex.Single
import retrofit2.http.*

public interface HttpApi {

    @FormUrlEncoded
    @POST("/api/search/pc/")
    fun searchMusic(@FieldMap paramMap: Map<String, String>): Single<SearchMusicResponse>

    @GET("/api/song/enhance/player/url/")
    fun musicResource(@QueryMap paramMap: Map<String, String>): Single<MusicResourceResponse>
}