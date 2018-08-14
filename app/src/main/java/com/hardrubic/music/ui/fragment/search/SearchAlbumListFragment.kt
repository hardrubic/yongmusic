package com.hardrubic.music.ui.fragment.search

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.interf.DialogBtnListener
import com.hardrubic.music.biz.interf.Searchable
import com.hardrubic.music.biz.vm.SearchViewModel
import com.hardrubic.music.ui.activity.AlbumDetailActivity
import com.hardrubic.music.ui.adapter.AlbumListAdapter
import com.hardrubic.music.ui.fragment.BaseFragment
import com.hardrubic.music.util.LoadingDialogUtil
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.util.*

class SearchAlbumListFragment : BaseFragment(), Searchable {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(mActivity).get(SearchViewModel::class.java)
    }

    private lateinit var adapter: AlbumListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search_result_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initView()
    }

    private fun initView() {
        adapter = AlbumListAdapter(Collections.emptyList())
        adapter.setOnItemClickListener { adapter, view, position ->
            val album = (adapter as AlbumListAdapter).getItem(position)!!
            AlbumDetailActivity.start(mActivity, album.albumId)
        }
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
    }

    private fun initData() {
        viewModel.albumData.observe(this, android.arch.lifecycle.Observer {
            adapter.setNewData(it)
            LoadingDialogUtil.getInstance().dismissLoadingDialog()
        })
    }

    override fun search(text: String) {
        viewModel.searchAlbum(text, Consumer { throwable ->
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