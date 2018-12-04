package com.hardrubic.music.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.KeyEvent
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.Toolbar
import com.hardrubic.music.R
import com.hardrubic.music.biz.interf.Searchable
import com.hardrubic.music.ui.adapter.MyViewPagerAdapter
import com.hardrubic.music.ui.fragment.search.SearchAlbumListFragment
import com.hardrubic.music.ui.fragment.search.SearchArtistListFragment
import com.hardrubic.music.ui.fragment.search.SearchMusicListFragment
import com.hardrubic.music.ui.widget.edittext.ClearableEditText
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity() {

    private val ANIMATOR_DURATION = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val searchView = ClearableEditText(this).apply {
            this.hint = getString(R.string.search)
            this.imeOptions = EditorInfo.IME_ACTION_SEARCH
            this.inputType = EditorInfo.TYPE_CLASS_TEXT
            this.setOnEditorActionListener { textVIew, actionId, event ->
                event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
            }
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
        tab.setupWithViewPager(vp_list)
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                applySearch(searchView.text.trim().toString())
            }
        })

        RxTextView.textChanges(searchView)
                .throttleLast(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map { it.toString() }
                .subscribe {
                    applySearch(it)
                }

        searchView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                searchView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                performEnterAnimation()
            }
        })
    }

    /**
     * 计算搜索栏在上一个页面的位置
     */
    private fun searchViewOriginalY(): Float {
        /*
        val originalScreenY = intent.getFloatExtra(SEARCH_VIEW_Y, et_text.y)

        val tv = TypedValue()
        this.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
        val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        val statusBarHeight = ScreenUtils.getStatusBarHeight(this)
        return originalScreenY - statusBarHeight - actionBarHeight
        */
        return 0F
    }

    private fun performEnterAnimation() {
        val alphaAnimator = buildAlphaAnimator(0F, 1F)
        alphaAnimator.start()
    }

    private fun performExitAnimation() {
        val alphaAnimator = buildAlphaAnimator(1F, 0F)
        alphaAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                finish()
                overridePendingTransition(0, 0)
            }
        })

        alphaAnimator.start()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        performExitAnimation()
    }

    private fun buildAlphaAnimator(from: Float, to: Float): ValueAnimator {
        val animator = ValueAnimator.ofFloat(from, to)
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            window.decorView.alpha = value
        }
        animator.duration = ANIMATOR_DURATION
        return animator
    }

    private fun applySearch(text: String) {
        if (text.isEmpty()) {
            return
        }

        val position = vp_list.currentItem
        val fragment = (vp_list.adapter as MyViewPagerAdapter).getItem(position)
        (fragment as? Searchable)?.search(text)
    }

    companion object {
        val SEARCH_VIEW_Y = "SEARCH_VIEW_Y"

        fun start(activity: Activity, searchViewY: Int) {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra(SEARCH_VIEW_Y, searchViewY)
            activity.startActivity(intent)
        }
    }
}
