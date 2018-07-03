package com.hardrubic.music.biz.resource

import android.content.Context
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.bo.MusicResourceBO
import com.hardrubic.music.util.FileUtil
import io.reactivex.functions.Consumer
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Mission
import zlc.season.rxdownload3.core.Status
import zlc.season.rxdownload3.core.Succeed
import java.util.concurrent.CountDownLatch

object MusicDownload {

    fun downloadSync(context: Context, bos: List<MusicResourceBO>, errorConsumer: Consumer<Throwable>) {
        val saveDir = FileUtil.getMusicCacheDir(context)

        val countDownLatch = CountDownLatch(bos.size)
        //LogUtil.d("begin download music,size ${bos.size}")
        for (bo in bos) {
            val saveName = bo.md5
            bo.path = saveDir + saveName
            val mission = Mission(bo.url, saveName, saveDir)
            RxDownload.create(mission, true)
                    .subscribe(Consumer<Status> { status ->
                        when (status) {
                            is Succeed ->
                                countDownLatch.countDown()
                        }
                    }, errorConsumer)
        }

        countDownLatch.await()

        //LogUtil.d("finish download music")
    }

    fun fillResource2Music(musics: List<Music>, bos: List<MusicResourceBO>) {
        for (music in musics) {
            val bo = bos.find { it.musicId == music.musicId } ?: continue

            music.path = bo.path
        }

    }
}