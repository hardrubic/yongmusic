package com.hardrubic.music.ui.fragment

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.R.id.rv_list
import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.ui.activity.AlbumDetailActivity
import com.hardrubic.music.ui.adapter.show.ShowAlbumAdapter
import kotlinx.android.synthetic.main.fragment_search_result_list.*

class ArtistAlbumFragment : BaseFragment() {

    private val albums: List<AlbumVO>
        get() = arguments?.getSerializable(Constant.Param.LIST) as? List<AlbumVO> ?: listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_artist_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        val adapter = ShowAlbumAdapter(albums)
        adapter.setOnItemClickListener { adapter, view, position ->
            val album = (adapter as ShowAlbumAdapter).getItem(position)!!
            AlbumDetailActivity.start(mActivity, album.albumId)
        }
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }
}