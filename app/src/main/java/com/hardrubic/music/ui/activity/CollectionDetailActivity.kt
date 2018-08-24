package com.hardrubic.music.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.CollectionDetailViewModel
import com.hardrubic.music.ui.fragment.CommonMusicListFragment
import kotlinx.android.synthetic.main.activity_collection_detail.*

class CollectionDetailActivity : BaseActivity() {

    private val collection by lazy {
        val id = intent.getLongExtra(Constant.Param.COLLECTION_ID, -1)
        viewModel.queryCollection(id)!!
    }

    private val viewModel: CollectionDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CollectionDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_collection_detail_action, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_edit) {
            Snackbar.make(fl_music_list, "todo", Snackbar.LENGTH_SHORT).show()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.collection)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()

        tv_name.text = collection.name

        val musicVOs = viewModel.queryCollectionMusicVOs(collection.id)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_music_list, CommonMusicListFragment.instance(musicVOs, network = false))
                .commit()
    }
}
