package com.hardrubic.music.ui.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.SelectCollectionViewModel
import com.hardrubic.music.ui.adapter.CollectionAdapter
import com.hardrubic.music.ui.fragment.CollectionNameDialogFragment
import com.hardrubic.music.ui.fragment.OnFinishCollectionNameListener
import kotlinx.android.synthetic.main.activity_select_collection.*

class SelectCollectionActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(SelectCollectionViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_collection)

        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar).apply {
            title = getString(R.string.select_collection)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val collectionAdapter = CollectionAdapter(viewModel.queryCollection())
        collectionAdapter.setOnItemClickListener { adapter, view, position ->
            if (position == 0) {
                addCollection()
            } else {
                adapter as CollectionAdapter
                val collection = adapter.getItem(position)!!
                finishSelect(collection.id)
            }
        }
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = collectionAdapter

        showMusicControl()
    }

    private fun addCollection() {
        val dialogFragment = CollectionNameDialogFragment()
        dialogFragment.listener = object : OnFinishCollectionNameListener {
            override fun onFinish(name: String) {
                val collectionId = viewModel.addCollection(name)
                finishSelect(collectionId)
            }
        }
        dialogFragment.show(supportFragmentManager, CollectionNameDialogFragment.TAG)
    }

    private fun finishSelect(collectionId: Long) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(Constant.Param.COLLECTION_ID, collectionId)
        })
        finish()
    }

    companion object {
        fun start(activity: FragmentActivity, musicId: Long) {
            val intent = Intent(activity, SelectCollectionActivity::class.java)
            intent.putExtra(Constant.Param.MUSIC_ID, musicId)
            activity.startActivityForResult(intent, Constant.RequestCode.SELECT_COLLECTION)
        }
    }
}
