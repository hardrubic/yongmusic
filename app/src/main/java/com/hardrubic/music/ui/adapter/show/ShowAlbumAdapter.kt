package com.hardrubic.music.ui.adapter.show

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hardrubic.music.R
import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.util.FormatUtil
import com.hardrubic.music.util.LoadImageUtil

class ShowAlbumAdapter(data: List<AlbumVO>)
    : BaseQuickAdapter<AlbumVO, BaseViewHolder>(R.layout.item_album, data) {

    override fun convert(baseViewHolder: BaseViewHolder, vo: AlbumVO) {
        val position = baseViewHolder.adapterPosition - headerLayoutCount

        if (!TextUtils.isEmpty(vo.picUrl)) {
            LoadImageUtil.loadFromNetwork(mContext, vo.picUrl, baseViewHolder.getView(R.id.iv_cover))
        }

        baseViewHolder.setText(R.id.tv_name, vo.name)

        val artistName = FormatUtil.formatArtistNames(vo.artistNames)

        val info = StringBuilder().append(artistName)
        if (vo.publishTime != null) {
            info.append(" ").append(FormatUtil.formatPublishTime(vo.publishTime))
        }
        baseViewHolder.setText(R.id.tv_info, info)
    }

}
