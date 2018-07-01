package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.listener.DialogBtnListener
import com.hardrubic.music.biz.listener.SearchRefreshListener
import com.hardrubic.music.biz.vm.ArtistDetailViewModel
import com.hardrubic.music.biz.vm.SearchViewModel
import com.hardrubic.music.ui.adapter.MusicListAdapter
import com.hardrubic.music.ui.fragment.BaseFragment
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_artist_desc.*
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.util.*

class ArtistDescFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_artist_desc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_desc.text = arguments?.getString(Constant.Param.NAME)
    }
}