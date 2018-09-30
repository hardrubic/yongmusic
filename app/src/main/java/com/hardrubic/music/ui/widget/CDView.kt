package com.hardrubic.music.ui.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
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

    override fun onDraw(canvas: Canvas) {
        //TODO 避免在onDraw做创建操作
        val bitmap = (drawable as BitmapDrawable).bitmap
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val circlePaint = Paint().apply {
            isAntiAlias = true
            shader = bitmapShader
        }
        canvas.drawCircle(width / 2F, height / 2F, width / 2F, circlePaint)
    }
}