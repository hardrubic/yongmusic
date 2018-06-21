package com.hardrubic.music.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hardrubic.music.R
import com.hardrubic.music.db.dataobject.Collection

class CollectionAdapter(data: List<Collection>)
    : BaseQuickAdapter<Collection, BaseViewHolder>(R.layout.item_collection, data) {

    override fun convert(baseViewHolder: BaseViewHolder, collection: Collection) {
        val position = baseViewHolder.adapterPosition - headerLayoutCount

        baseViewHolder.setText(R.id.tv_name, collection.name)

        if (collection.musicNum >= 0) {
            baseViewHolder.setText(R.id.tv_artist, mContext.getString(R.string.music_num, collection.musicNum))
        }
    }

}
