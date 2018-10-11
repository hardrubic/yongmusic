package com.hardrubic.music.biz.search

import android.support.v7.app.AppCompatActivity
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import io.reactivex.functions.Consumer

class SearchErrorAction(val activity: AppCompatActivity) : Consumer<Throwable> {
    override fun accept(throwable: Throwable) {
        ShowExceptionHelper.show(activity, throwable, null)
        throwable.printStackTrace()
    }
}