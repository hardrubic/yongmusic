package com.hardrubic.music.ui.activity

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hardrubic.music.R
import com.hardrubic.music.ui.fragment.MusicControlFragment

open class BaseActivity : AppCompatActivity() {

    private var musicControlFragment: MusicControlFragment? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    protected fun showMusicControl() {
        val ft = supportFragmentManager.beginTransaction()
        if (musicControlFragment == null) {
            musicControlFragment = MusicControlFragment.newInstance()
            ft.add(R.id.fl_music_control, musicControlFragment)
        } else {
            ft.show(musicControlFragment)
        }
        ft.commit()
    }
}