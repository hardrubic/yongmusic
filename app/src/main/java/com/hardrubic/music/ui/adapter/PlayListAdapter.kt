package com.hardrubic.music.ui.adapter

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hardrubic.music.R
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.util.FormatUtil

class PlayListAdapter(data: List<Music>)
    : BaseQuickAdapter<Music, BaseViewHolder>(R.layout.item_play_list, data) {

    var playingMusicId: Long? = null

    override fun convert(baseViewHolder: BaseViewHolder, music: Music) {
        val position = baseViewHolder.adapterPosition

        baseViewHolder.setText(R.id.tv_name, music.name)
        if (playingMusicId == null || playingMusicId != music.musicId) {
            baseViewHolder.setTextColor(R.id.tv_name, ContextCompat.getColor(mContext, R.color.primary_text_color))
        } else {
            baseViewHolder.setTextColor(R.id.tv_name, ContextCompat.getColor(mContext, R.color.colorPrimary))
        }

        baseViewHolder.setText(R.id.tv_artist, "- ${FormatUtil.formatArtistNames(music.artistNames)}")
        baseViewHolder.addOnClickListener(R.id.iv_delete)
    }
}
