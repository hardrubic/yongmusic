package com.hardrubic.music.network.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.greendao.converter.PropertyConverter
import java.util.*

class IdsConverter : PropertyConverter<List<Long>, String> {

    override fun convertToEntityProperty(databaseValue: String): List<Long>? {
        return Gson().fromJson<List<Long>>(databaseValue, type)
    }

    override fun convertToDatabaseValue(entityProperty: List<Long>): String {
        return Gson().toJson(entityProperty, type)
    }

    companion object {
        private val type = object : TypeToken<ArrayList<Long>>() {
        }.type
    }
}
