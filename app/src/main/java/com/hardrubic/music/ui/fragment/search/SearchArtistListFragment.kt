package com.hardrubic.music.ui.fragment.search

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.listener.DialogBtnListener
import com.hardrubic.music.biz.listener.SearchRefreshListener
import com.hardrubic.music.biz.vm.SearchViewModel
import com.hardrubic.music.ui.activity.ArtistDetailActivity
import com.hardrubic.music.ui.adapter.ArtistListAdapter
import com.hardrubic.music.ui.fragment.BaseFragment
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.util.*

class SearchArtistListFragment : BaseFragment(), SearchRefreshListener {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(mActivity).get(SearchViewModel::class.java)
    }

    private lateinit var adapter: ArtistListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search_result_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initView()
    }

    private fun initView() {
        adapter = ArtistListAdapter(Collections.emptyList())
        adapter.setOnItemClickListener { adapter, view, position ->
            val artist = (adapter as ArtistListAdapter).getItem(position)!!
            startActivity(Intent(activity, ArtistDetailActivity::class.java).apply {
                putExtra(Constant.Param.ARTIST_ID, artist.artistId)
            })
        }
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
    }

    private fun initData() {
        viewModel.artistData.observe(this, android.arch.lifecycle.Observer {
            adapter.setNewData(it)
        })
    }

    override fun search(text: String) {
        viewModel.searchArtist(text, Consumer { throwable ->
            ShowExceptionHelper.show(mActivity, throwable, object : DialogBtnListener {
                override fun onClickOkListener(dialog: DialogInterface?) {
                    search(text)
                    dialog?.dismiss()
                }

                override fun onClickCancelListener(dialog: DialogInterface?) {
                    dialog?.dismiss()
                }
            })
            throwable.printStackTrace()
        })
    }
}