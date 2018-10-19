package com.hardrubic.music.ui.activity

import android.os.Bundle
import android.widget.Toast
import com.hardrubic.music.R
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.playstate.PauseCommand
import com.hardrubic.music.service.MusicServiceControl
import com.hardrubic.music.util.file.FilePathUtil
import kotlinx.android.synthetic.main.activity_record_audio.*
import java.io.File
import java.text.SimpleDateFormat

class RecordAudioActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_audio)

        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.record_audio)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MusicServiceControl.runInMusicService(this) {
            RemoteControl.executeCommand(PauseCommand(it))
        }

        val tmpRecordFileName = FilePathUtil.getRecordAudioTmpFile(this)

        ll_record_audio.setTmpRecordPath(tmpRecordFileName)
        ll_record_audio.setRecordAudioCompletedListener {
            saveAndRename(it)
        }
        //ll_record_audio.startRecord()
    }

    private fun saveAndRename(path: String) {
        val tmpFile = File(path)
        val newName = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + ".mp3"

        if (tmpFile.exists()) {
            val newFile = File(tmpFile.parent + File.separator + newName)
            if (tmpFile.renameTo(newFile)) {
                tmpFile.delete()
                Toast.makeText(this, "生成录音成功，保存至[${newFile.absolutePath}]", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        ll_record_audio.stopRecord()
        super.onBackPressed()
    }
}
