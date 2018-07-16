package com.hardrubic.music.util

import android.support.v4.graphics.drawable.DrawableCompat
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.widget.ImageView


object DrawableUtil {
    fun setImageViewColor(view: ImageView, colorResId: Int) {
        val modeDrawable = view.drawable.mutate()
        val temp = DrawableCompat.wrap(modeDrawable)
        val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(view.context, colorResId))
        DrawableCompat.setTintList(temp, colorStateList)
        view.setImageDrawable(temp)
    }
}