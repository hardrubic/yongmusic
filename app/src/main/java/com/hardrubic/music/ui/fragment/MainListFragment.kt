package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.MainViewModel
import com.hardrubic.music.ui.activity.CollectionDetailActivity
import com.hardrubic.music.ui.activity.LocalMusicActivity
import com.hardrubic.music.ui.activity.RecentActivity
import com.hardrubic.music.ui.adapter.CollectionAdapter
import kotlinx.android.synthetic.main.fragment_main_list.*
import java.util.*

class MainListFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    private lateinit var collectionAdapter: CollectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMyLoveCollection()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_local_music.setOnClickListener {
            startActivity(Intent(activity, LocalMusicActivity::class.java))
        }
        tv_recent.setOnClickListener {
            startActivity(Intent(activity, RecentActivity::class.java))
        }

        ll_love.setOnClickListener {
            startCollectionDetail(Constant.LOVE_COLLECTION_ID)
        }

        iv_add_collection.setOnClickListener {
            val dialogFragment = CollectionNameDialogFragment()
            dialogFragment.listener = object : OnFinishCollectionNameListener {
                override fun onFinish(name: String) {
                    viewModel.addCollection(name)
                    refreshCollectionList()
                }
            }
            dialogFragment.show(activity!!.supportFragmentManager, CollectionNameDialogFragment.TAG)
        }
        iv_edit_collection.setOnClickListener {
            Toast.makeText(activity, "todo歌单管理", Toast.LENGTH_SHORT).show()
        }

        collectionAdapter = CollectionAdapter(Collections.emptyList())
        collectionAdapter.setOnItemClickListener { adapter, view, position ->
            adapter as CollectionAdapter
            val collection = adapter.getItem(position)!!

            startCollectionDetail(collection.id)
        }
        rv_collection.layoutManager = LinearLayoutManager(activity)
        rv_collection.adapter = collectionAdapter
    }

    override fun onResume() {
        super.onResume()

        tv_love_num.text = getString(R.string.num, viewModel.queryLoveCollectionMusicNum())
        refreshCollectionList()
    }

    private fun refreshCollectionList() {
        collectionAdapter.setNewData(viewModel.queryCollection())
    }

    private fun initMyLoveCollection() {
        val myLoveCollection = viewModel.queryCollection(Constant.LOVE_COLLECTION_ID)
        if (myLoveCollection == null) {
            viewModel.addCollection(getString(R.string.my_love), Constant.LOVE_COLLECTION_ID)
        }
    }

    private fun startCollectionDetail(collectionId: Long) {
        val intent = Intent(activity, CollectionDetailActivity::class.java)
        intent.putExtra(Constant.Param.COLLECTION_ID, collectionId)

        startActivity(intent)
    }
}