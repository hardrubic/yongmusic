package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DrawableUtils
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.MainViewModel
import com.hardrubic.music.ui.activity.AlbumDetailActivity
import com.hardrubic.music.ui.activity.CollectionDetailActivity
import com.hardrubic.music.ui.activity.LocalMusicActivity
import com.hardrubic.music.ui.activity.RecentActivity
import com.hardrubic.music.ui.adapter.CollectionAdapter
import com.hardrubic.music.util.DrawableUtil
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

        ll_local.setOnClickListener {
            startActivity(Intent(activity, LocalMusicActivity::class.java))
        }
        tv_recent.setOnClickListener {
            startActivity(Intent(activity, RecentActivity::class.java))
        }
        ll_love.setOnClickListener {
            startCollectionDetail(Constant.LOVE_COLLECTION_ID)
        }
        ll_store.setOnClickListener {
            //just for test
            startActivity(Intent(activity, AlbumDetailActivity::class.java).apply {
                putExtra(Constant.Param.ALBUM_ID, 29597L)
            })
            /*
            startActivity(Intent(activity, ArtistDetailActivity::class.java).apply {
                putExtra(Constant.Param.ARTIST_ID, 6452)
            })
            */
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
        DrawableUtil.setImageViewColor(iv_edit_collection, R.color.second_text_color)

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

        //todo async
        tv_love_num.text = getString(R.string.num_with_bracket, viewModel.queryLoveCollectionMusicNum())
        tv_local_num.text = getString(R.string.num_with_bracket, viewModel.queryLocalMusicNum())
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