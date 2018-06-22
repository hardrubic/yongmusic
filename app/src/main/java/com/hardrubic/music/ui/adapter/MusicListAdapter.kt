package com.hardrubic.music.ui.adapter

import android.text.format.Formatter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hardrubic.music.R
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.util.FormatUtil

class MusicListAdapter(data: List<Music>)
    : BaseQuickAdapter<Music, BaseViewHolder>(R.layout.item_music, data) {

    override fun convert(baseViewHolder: BaseViewHolder, music: Music) {
        val position = baseViewHolder.adapterPosition - headerLayoutCount

        val formatDuration = FormatUtil.formatDuration(music.duration.toLong())

        baseViewHolder.setText(R.id.tv_name, music.name)
        baseViewHolder.setText(R.id.tv_artist, FormatUtil.formatArtistNames(music.artistNames))
        baseViewHolder.setText(R.id.tv_duration, formatDuration)
        baseViewHolder.setText(R.id.tv_size, if (music.size != null) Formatter.formatFileSize(mContext, music.size.toLong()) else "--")
    }

}
