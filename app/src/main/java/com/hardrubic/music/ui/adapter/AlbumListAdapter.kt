package com.hardrubic.music.ui.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hardrubic.music.R
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.util.FormatUtil
import com.hardrubic.music.util.LoadImageUtil

class AlbumListAdapter(data: List<Album>)
    : BaseQuickAdapter<Album, BaseViewHolder>(R.layout.item_album, data) {

    override fun convert(baseViewHolder: BaseViewHolder, album: Album) {
        val position = baseViewHolder.adapterPosition - headerLayoutCount

        if (!TextUtils.isEmpty(album.picUrl)) {
            LoadImageUtil.loadFromNetwork(mContext, album.picUrl, baseViewHolder.getView(R.id.iv_cover))
        }

        baseViewHolder.setText(R.id.tv_name, album.name)

        val artistName = FormatUtil.formatArtistNames(album.artistNames)

        val info = StringBuilder().append(artistName)
        if (album.publishTime != null) {
            info.append(" ").append(FormatUtil.formatPublishTime(album.publishTime))
        }
        baseViewHolder.setText(R.id.tv_info, info)
    }

}
