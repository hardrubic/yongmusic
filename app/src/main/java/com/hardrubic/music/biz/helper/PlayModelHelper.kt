package com.hardrubic.music.biz.helper

import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.util.PreferencesUtil

object PlayModelHelper {

    fun loadPlayModel(): Int {
        return PreferencesUtil.instance.getInt(Constant.SpKey.PLAY_MODEL, Constant.PlayModel.LIST)
    }

    fun updatePlayModel(model: Int) {
        PreferencesUtil.instance.putInt(Constant.SpKey.PLAY_MODEL, model)
    }

    fun nextPlayModel(currentModel: Int): Int {
        when (currentModel) {
            Constant.PlayModel.LIST -> return Constant.PlayModel.SINGLE
            Constant.PlayModel.SINGLE -> return Constant.PlayModel.RANDOM
            Constant.PlayModel.RANDOM -> return Constant.PlayModel.LIST
        }
        return Constant.PlayModel.LIST
    }

    fun getPlayModelStringId(model: Int): Int {
        return when (model) {
            Constant.PlayModel.LIST -> R.string.play_by_list
            Constant.PlayModel.RANDOM -> R.string.play_by_random
            Constant.PlayModel.SINGLE -> R.string.play_by_single
            else -> R.string.play_by_list
        }
    }

    fun getPlayModelIconId(model: Int): Int {
        return when (model) {
            Constant.PlayModel.LIST -> R.mipmap.ic_model_by_list
            Constant.PlayModel.RANDOM -> R.mipmap.ic_model_by_random
            Constant.PlayModel.SINGLE -> R.mipmap.ic_model_by_single
            else -> R.mipmap.ic_model_by_list
        }
    }
}