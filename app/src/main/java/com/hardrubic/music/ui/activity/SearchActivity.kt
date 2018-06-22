package com.hardrubic.music.ui.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.AppCompatEditText
import android.widget.Toolbar
import com.hardrubic.music.R
import com.hardrubic.music.biz.listener.SearchRefreshListener
import com.hardrubic.music.ui.adapter.MyViewPagerAdapter
import com.hardrubic.music.ui.fragment.search.SearchAlbumListFragment
import com.hardrubic.music.ui.fragment.search.SearchArtistListFragment
import com.hardrubic.music.ui.fragment.search.SearchMusicListFragment
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val searchView = AppCompatEditText(this).apply {
            this.hint = getString(R.string.search)
        }
        toolbar.addView(searchView, Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT))
        toolbar.title = ""

        showMusicControl()

        tab.tabMode = TabLayout.MODE_FIXED
        vp_list.adapter = MyViewPagerAdapter(supportFragmentManager).apply {
            addFragment(SearchMusicListFragment(), getString(R.string.single_music))
            addFragment(SearchArtistListFragment(), getString(R.string.artist))
            addFragment(SearchAlbumListFragment(), getString(R.string.album))
        }
        tab.setupWithViewPager(vp_list)

        RxTextView.textChanges(searchView)
                .throttleLast(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map { it.toString() }
                .subscribe {
                    applySearch(it)
                }
    }

    private fun applySearch(text: String) {
        val fragments = supportFragmentManager.fragments
        fragments.forEach {
            (it as? SearchRefreshListener)?.search(text)
        }
    }
}
