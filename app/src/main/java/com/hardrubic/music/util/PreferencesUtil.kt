package com.hardrubic.music.util

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.Delegates

class PreferencesUtil private constructor(context: Context) {

    fun putString(key: String, value: String): Boolean {
        return defaultSp.edit().putString(key, value).commit()
    }

    @JvmOverloads
    fun getString(key: String, defaultValue: String = ""): String {
        return defaultSp.getString(key, defaultValue)
    }

    fun putInt(key: String, value: Int): Boolean {
        return defaultSp.edit().putInt(key, value).commit()
    }

    @JvmOverloads
    fun getInt(key: String, defaultValue: Int = -1): Int {
        return defaultSp.getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long): Boolean {
        return defaultSp.edit().putLong(key, value).commit()
    }

    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = -1): Long {
        return defaultSp.getLong(key, defaultValue)
    }

    fun putFloat(key: String, value: Float): Boolean {
        return defaultSp.edit().putFloat(key, value).commit()
    }

    @JvmOverloads
    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        return defaultSp.getFloat(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean): Boolean {
        return defaultSp.edit().putBoolean(key, value).commit()
    }

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return defaultSp.getBoolean(key, defaultValue)
    }

    fun deleteKey(key: String) {
        defaultSp.edit().remove(key)
    }

    companion object {
        private val DEFAULT_SP_NAME = "default_sp"

        private lateinit var context: Context
        private lateinit var defaultSp: SharedPreferences
        var instance: PreferencesUtil by Delegates.notNull()

        fun initializeInstance(context: Context) {
            this.context = context
            this.defaultSp = context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            this.instance = PreferencesUtil(context)
        }
    }
}
