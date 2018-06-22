package com.hardrubic.music.network.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.greendao.converter.PropertyConverter
import java.util.*

class NamesConverter : PropertyConverter<List<String>, String> {

    override fun convertToEntityProperty(databaseValue: String): List<String>? {
        return Gson().fromJson<List<String>>(databaseValue, type)
    }

    override fun convertToDatabaseValue(entityProperty: List<String>): String {
        return Gson().toJson(entityProperty, type)
    }

    companion object {
        private val type = object : TypeToken<ArrayList<String>>() {
        }.type
    }
}
