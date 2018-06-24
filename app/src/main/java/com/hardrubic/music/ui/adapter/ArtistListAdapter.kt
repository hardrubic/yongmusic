package com.hardrubic.music.ui.adapter

import android.text.TextUtils
import android.text.format.Formatter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hardrubic.music.R
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.util.FormatUtil
import com.hardrubic.music.util.LoadImageUtil

class ArtistListAdapter(data: List<Artist>)
    : BaseQuickAdapter<Artist, BaseViewHolder>(R.layout.item_artist, data) {

    override fun convert(baseViewHolder: BaseViewHolder, artist: Artist) {
        val position = baseViewHolder.adapterPosition - headerLayoutCount

        baseViewHolder.setText(R.id.tv_artist, artist.name)
        if (artist.alias.isNotEmpty()) {
            baseViewHolder.setText(R.id.tv_alias, FormatUtil.formatAlias(artist.alias))
            baseViewHolder.setGone(R.id.tv_alias, true)
        }else{
            baseViewHolder.setGone(R.id.tv_alias, false)
        }

        if (!TextUtils.isEmpty(artist.picUrl)) {
            LoadImageUtil.loadFromNetwork(mContext, artist.picUrl, baseViewHolder.getView(R.id.iv_cover))
        }
    }

}
