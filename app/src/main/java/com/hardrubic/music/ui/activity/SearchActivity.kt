package com.hardrubic.music.ui.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.AppCompatEditText
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
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
            this.imeOptions = EditorInfo.IME_ACTION_SEARCH
            this.inputType = EditorInfo.TYPE_CLASS_TEXT;
            this.setOnEditorActionListener { textVIew, actionId, event ->
                event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
            }
            //todo 光标颜色，清空按钮
        }
        toolbar.addView(searchView, Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT))
        toolbar.title = ""

        showMusicControl()

        vp_list.adapter = MyViewPagerAdapter(supportFragmentManager).apply {
            addFragment(SearchMusicListFragment(), getString(R.string.single_music))
            addFragment(SearchArtistListFragment(), getString(R.string.artist))
            addFragment(SearchAlbumListFragment(), getString(R.string.album))
        }
        tab.tabMode = TabLayout.MODE_FIXED
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                applySearch(searchView.text.trim().toString())
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
        tab.setupWithViewPager(vp_list)

        RxTextView.textChanges(searchView)
                .throttleLast(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map { it.toString() }
                .subscribe {
                    applySearch(it)
                }
    }

    private fun applySearch(text: String) {
        val position = vp_list.currentItem
        val fragment = (vp_list.adapter as MyViewPagerAdapter).getItem(position)
        (fragment as? SearchRefreshListener)?.search(text)
    }
}
