package com.hardrubic.music.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hardrubic.music.R
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.util.FormatUtil

class AlbumListAdapter(data: List<Album>)
    : BaseQuickAdapter<Album, BaseViewHolder>(R.layout.item_album, data) {

    override fun convert(baseViewHolder: BaseViewHolder, album: Album) {
        val position = baseViewHolder.adapterPosition - headerLayoutCount

        baseViewHolder.setText(R.id.tv_name, album.name)
        if (album.alias != null && album.alias.isNotEmpty()) {
            baseViewHolder.setText(R.id.tv_alias, album.alias.joinToString(separator = "/", prefix = "(", postfix = ")"))
            baseViewHolder.setGone(R.id.tv_alias, false)
        } else {
            baseViewHolder.setGone(R.id.tv_alias, true)
        }

        val artistName = FormatUtil.formatArtistNames(album.artistNames)

        val info = StringBuilder().append(artistName)
        if (album.publishTime != null) {
            info.append(" ").append(FormatUtil.formatPublishTime(album.publishTime))
        }
        baseViewHolder.setText(R.id.tv_info, info)
    }

}
