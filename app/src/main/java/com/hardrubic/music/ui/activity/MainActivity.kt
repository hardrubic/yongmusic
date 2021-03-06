package com.hardrubic.music.ui.activity

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.Toast
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.LoginInfo
import com.hardrubic.music.biz.adapter.MusicEntityAdapter
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.vm.MainViewModel
import com.hardrubic.music.entity.aidl.MusicAidl
import com.hardrubic.music.service.MusicService
import com.hardrubic.music.service.OverlayService
import com.hardrubic.music.service.ServiceHelper
import com.hardrubic.music.ui.adapter.MyViewPagerAdapter
import com.hardrubic.music.ui.fragment.DiscoverFragment
import com.hardrubic.music.ui.fragment.MainListFragment
import com.hardrubic.music.util.PreferencesUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showMusicControl()

        initMusicService()
        initToolBarAndDrawerLayout()
        applyPermission()
    }

    private fun initMusicService() {

        val intent = Intent(this, MusicService::class.java)
        intent.putExtra(Constant.Param.PLAY_MODEL, PreferencesUtil.instance.getInt(Constant.SpKey.PLAY_MODEL))

        CurrentPlayingHelper.getPlayingMusicId()?.let {
            val playingMusic = viewModel.queryMusic(it)
            if (playingMusic != null) {
                intent.putExtra(Constant.Param.CURRENT_MUSIC, MusicEntityAdapter.toMusicAidl(playingMusic))
            }
        }

        val playList: List<MusicAidl> = PlayListHelper.list()
                .asSequence()
                .mapNotNull { viewModel.queryMusic(it) }
                .map {
                    MusicEntityAdapter.toMusicAidl(it)
                }
                .toList()
        if (playList.isNotEmpty()) {
            intent.putParcelableArrayListExtra(Constant.Param.LIST, ArrayList<MusicAidl>(playList))
        }

        startService(intent)
    }

    private fun initOverlayService() {
        if (Settings.canDrawOverlays(this)) {
            if (!ServiceHelper.isServiceWork(this, OverlayService::class.java.name)) {
                val intent = Intent(this, OverlayService::class.java)
                CurrentPlayingHelper.getPlayingMusicId()?.let {
                    val playingMusic = viewModel.queryMusic(it)
                    if (playingMusic != null) {
                        intent.putExtra(Constant.Param.CURRENT_MUSIC, MusicEntityAdapter.toMusicAidl(playingMusic))
                    }
                }

                startService(intent)
            } else {
                val intent = Intent(this, OverlayService::class.java)
                stopService(intent)
            }
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.hint_get_overlay_permission)
            builder.setPositiveButton(R.string.ok) { dialog, which ->
                val action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                val uri = Uri.parse("package:$packageName")
                startActivityForResult(Intent(action, uri), Constant.RequestCode.GET_OVERLAY_PERMISSION)
            }
            builder.setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun initToolBarAndDrawerLayout() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val nav_view_header = nav_view.inflateHeaderView(R.layout.nav_header_main)
        nav_view_header.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        nav_view.setNavigationItemSelectedListener(this)

        ll_search.setOnClickListener {
            val location = IntArray(2)
            ll_search.getLocationOnScreen(location)
            val searchViewY = location[1]
            SearchActivity.start(this@MainActivity, searchViewY)
            overridePendingTransition(0, 0)
        }

        val paperAdapter = MyViewPagerAdapter(supportFragmentManager)
        paperAdapter.addFragment(MainListFragment(), getString(R.string.my))
        paperAdapter.addFragment(DiscoverFragment(), getString(R.string.discover))

        vp_main.adapter = paperAdapter
        tl_top.setupWithViewPager(vp_main)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_record_audio -> {
                startRecordAudioActivity()
            }
            R.id.nav_import_from -> {
                if (LoginInfo.isLogin) {
                    viewModel.importFrom()
                    Toast.makeText(this, "导入成功1", Toast.LENGTH_LONG).show()
                } else {
                    startActivityForResult(Intent(this, LoginActivity::class.java), Constant.RequestCode.LOGIN_IN)
                }
            }
            R.id.nav_overlay -> {
                initOverlayService()
            }
            R.id.nav_schedule_close -> {
                Toast.makeText(this, "敬请期待", Toast.LENGTH_LONG).show()
            }
            R.id.nav_night -> {
                Toast.makeText(this, "敬请期待", Toast.LENGTH_LONG).show()
            }
            R.id.nav_clear_cache -> {
                Toast.makeText(this, "敬请期待", Toast.LENGTH_LONG).show()
            }
            R.id.nav_about -> {
                Toast.makeText(this, "敬请期待", Toast.LENGTH_LONG).show()
            }
            R.id.nav_exit -> {
                stopService(Intent(this, MusicService::class.java))
                finish()
            }
        }

        //drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun applyPermission() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                    } else {
                        showNeedPermissionDialog()
                    }
                }
    }

    private fun startRecordAudioActivity() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                .subscribe { granted ->
                    if (granted) {
                        val intent = Intent(this, RecordAudioActivity::class.java)
                        startActivity(intent)
                    }
                }
    }

    private fun showNeedPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.hint)
        builder.setMessage(R.string.hint_need_permission)
        builder.setPositiveButton(R.string.get) { dialog, which -> applyPermission() }
        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.dismiss()
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constant.RequestCode.LOGIN_IN -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.importFrom()
                    Toast.makeText(this, "导入成功2", Toast.LENGTH_LONG).show()
                }
            }
            Constant.RequestCode.GET_OVERLAY_PERMISSION -> {
                initOverlayService()
            }
        }
    }
}
