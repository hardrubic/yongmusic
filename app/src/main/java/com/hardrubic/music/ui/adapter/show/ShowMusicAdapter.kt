package com.hardrubic.music.ui.adapter.show

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hardrubic.music.R
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.util.FormatUtil

class ShowMusicAdapter(data: List<MusicVO>)
    : BaseQuickAdapter<MusicVO, BaseViewHolder>(R.layout.item_music, data) {

    override fun convert(baseViewHolder: BaseViewHolder, vo: MusicVO) {
        val position = baseViewHolder.adapterPosition - headerLayoutCount

        val formatArtistName = FormatUtil.formatArtistNames(vo.artistNames)

        baseViewHolder.setText(R.id.tv_name, vo.name)
        baseViewHolder.setText(R.id.tv_info, "$formatArtistName - ${vo.albumName}")

        if (vo.duration >= 0) {
            val formatDuration = FormatUtil.formatDuration(vo.duration.toLong())
            baseViewHolder.setText(R.id.tv_duration, formatDuration)
            baseViewHolder.setGone(R.id.tv_duration, true)
        } else {
            baseViewHolder.setGone(R.id.tv_duration, false)
        }

        if (vo.valid) {
            baseViewHolder.setTextColor(R.id.tv_name, ContextCompat.getColor(mContext, R.color.primary_text_color))
        } else {
            baseViewHolder.setTextColor(R.id.tv_name, ContextCompat.getColor(mContext, R.color.second_text_color))
        }
    }

    fun addMoreData(vos: List<MusicVO>) {
        data.addAll(vos)
        notifyDataSetChanged()
    }

}
