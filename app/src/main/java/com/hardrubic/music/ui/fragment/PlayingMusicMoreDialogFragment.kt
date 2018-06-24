package com.hardrubic.music.ui.fragment

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.PlayingViewModel
import com.hardrubic.music.ui.activity.SelectCollectionActivity
import kotlinx.android.synthetic.main.fragment_playing_musc_more.*

class PlayingMusicMoreDialogFragment : AppCompatDialogFragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(PlayingViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_playing_musc_more, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(this.context!!, this.theme)
    }

    private fun initView() {
        val playingMusic = viewModel.playingMusic!!
        tv_name.text = playingMusic.name

        tv_save_to_collection.setOnClickListener {
            SelectCollectionActivity.start(activity!!, playingMusic.musicId)
            dismiss()
        }
    }

    companion object {
        public val TAG = PlayingMusicMoreDialogFragment::class.java.simpleName
    }
}