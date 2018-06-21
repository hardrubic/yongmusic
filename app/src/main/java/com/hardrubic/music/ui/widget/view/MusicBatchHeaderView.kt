package com.hardrubic.music.ui.widget.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.hardrubic.music.R

class MusicBatchHeaderView : FrameLayout {

    var listener: OnMusicBatchHeaderViewListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_music_list_head, this)
        findViewById<LinearLayout>(R.id.ll_main).setOnClickListener {
            listener?.selectAll()
        }

        refreshNum(0)
    }

    fun refreshNum(num: Int) {
        findViewById<TextView>(R.id.tv_music_num).text = resources.getString(R.string.music_num, num)
    }
}

interface OnMusicBatchHeaderViewListener {
    fun selectAll()
}
