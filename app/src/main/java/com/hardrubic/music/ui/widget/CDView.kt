package com.hardrubic.music.ui.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.ImageView


class CDView : ImageView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val rotateAnimation: ObjectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)

    init {
        rotateAnimation.duration = 30 * 1000
        rotateAnimation.repeatCount = -1
        rotateAnimation.interpolator = LinearInterpolator()
    }

    fun startRotate() {
        if (rotateAnimation.isPaused) {
            rotateAnimation.resume()
        } else {
            rotateAnimation.start()
        }
    }

    fun pauseRotate() {
        rotateAnimation.pause()
    }

    fun resetRotate() {
        rotateAnimation.cancel()
    }
}