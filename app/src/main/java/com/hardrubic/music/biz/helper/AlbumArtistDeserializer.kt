package com.hardrubic.music.biz.helper

import com.google.gson.*
import com.hardrubic.music.network.response.entity.NeteaseAlbum
import com.hardrubic.music.network.response.entity.NeteaseArtist
import java.lang.reflect.Type

class AlbumArtistDeserializer : JsonDeserializer<NeteaseAlbum> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NeteaseAlbum {
        val jsonObject: JsonObject = json.asJsonObject.getAsJsonObject("artist")

        return if (jsonObject.isJsonArray) {
            Gson().fromJson(json, NeteaseAlbum::class.java)
        } else {
            NeteaseAlbum().apply {
                artists = listOf(context.deserialize(json, NeteaseArtist::class.java) as NeteaseArtist)
            }
        }
    }

}
