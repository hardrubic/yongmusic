package com.hardrubic.music.biz.helper

import android.text.TextUtils
import com.hardrubic.music.Constant
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.util.PreferencesUtil

object PlayListHelper {

    fun add(music: Music): Boolean {
        return add(listOf(music))
    }

    fun add(musics: List<Music>): Boolean {
        val existIdsStr: String? = PreferencesUtil.instance.getString(Constant.SpKey.PLAY_LIST)

        val addIds = musics.map { it.musicId.toString() }.toMutableList()
        val existIds = mutableListOf<String>()
        if (!TextUtils.isEmpty(existIdsStr)) {
            existIds.addAll(existIdsStr!!.split(","))
        }

        if (existIds.containsAll(addIds)) {
            return false
        }

        val result = mutableSetOf<String>()
        if (existIds.isNotEmpty()) {
            result.addAll(existIds)
        }
        result.addAll(addIds)

        PreferencesUtil.instance.putString(Constant.SpKey.PLAY_LIST, TextUtils.join(",", result))
        return true
    }

    fun replace(musics: List<Music>) {
        val ids = musics.map { it.musicId }
        PreferencesUtil.instance.putString(Constant.SpKey.PLAY_LIST, TextUtils.join(",", ids))
    }

    fun delete(musicId: Long) {
        val existIdsStr: String = PreferencesUtil.instance.getString(Constant.SpKey.PLAY_LIST)
        val existIds = existIdsStr.split(",").toMutableList()

        if (existIds.remove(musicId.toString())) {
            PreferencesUtil.instance.putString(Constant.SpKey.PLAY_LIST, TextUtils.join(",", existIds))
        }
    }

    fun deleteAll() {
        PreferencesUtil.instance.putString(Constant.SpKey.PLAY_LIST, "")
    }

    fun list(): List<Long> {
        val exist: String = PreferencesUtil.instance.getString(Constant.SpKey.PLAY_LIST)
        return exist.split(",").filter { it != "" }.map { it.toLong() }
    }
}