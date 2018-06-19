package com.hardrubic.music.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.R
import com.hardrubic.music.ui.activity.LocalMusicActivity
import com.hardrubic.music.ui.activity.RecentActivity
import kotlinx.android.synthetic.main.fragment_main_list.*

class MainListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}