package com.hardrubic.music.biz.resource

import android.content.Context
import com.hardrubic.music.biz.interf.MusicResourceListener
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.bo.MusicResourceBO
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.util.FileUtil
import io.reactivex.functions.Consumer
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Mission
import zlc.season.rxdownload3.core.Status
import zlc.season.rxdownload3.core.Succeed
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class MusicResourceDownload(val httpService: HttpService) {

    private object ProgressWeight {
        const val MUSIC_DETAIL = 1
        const val MUSIC_RESOURCE = 1
        const val MUSIC_DOWNLOAD = 100
        const val TOTAL = MUSIC_DETAIL + MUSIC_RESOURCE + MUSIC_DOWNLOAD
    }

    private val PROGRESS_MAX = 10000

    fun downloadMusicResource(context: Context, musicIds: List<Long>, musicResourceListener: MusicResourceListener? = null) {
        var currentProgress = 0

        fun refreshProgress(weightFloat: Float) {
            currentProgress += (weightFloat / ProgressWeight.TOTAL * PROGRESS_MAX).toInt()
            if (currentProgress >= PROGRESS_MAX) {
                currentProgress = PROGRESS_MAX - 1
            }
            musicResourceListener?.onProgress(currentProgress, PROGRESS_MAX)
        }

        val musicFileDownloadListener = object : MusicResourceDownload.MusicFileDownloadListener {
            override fun onSuccess() {
            }

            override fun onError(e: Throwable) {
                musicResourceListener?.onError(e)
            }

            override fun onFinishOne(total: Int) {
                val eachWeight = ProgressWeight.MUSIC_DOWNLOAD / total.toFloat()
                refreshProgress(eachWeight)
            }
        }

        thread {
            val countDownLatch = CountDownLatch(2)
            var saveMusics = listOf<Music>()
            var musicResourceBOs = listOf<MusicResourceBO>()

            httpService.applyMusicDetail(musicIds)
                    .subscribe(Consumer {
                        saveMusics = it
                        refreshProgress(ProgressWeight.MUSIC_DETAIL.toFloat())
                        countDownLatch.countDown()
                    }, Consumer {
                        musicResourceListener?.onError(it)
                    })

            httpService.applyMusicResource(musicIds)
                    .subscribe(Consumer {
                        downloadFileSync(context, it, musicFileDownloadListener)
                        musicResourceBOs = it
                        refreshProgress(ProgressWeight.MUSIC_RESOURCE.toFloat())
                        countDownLatch.countDown()
                    }, Consumer {
                        musicResourceListener?.onError(it)
                    })

            countDownLatch.await()

            fillResource2Music(saveMusics, musicResourceBOs)
            musicResourceListener?.onSuccess(saveMusics)
        }
    }

    private fun downloadFileSync(context: Context, bos: List<MusicResourceBO>, musicFileDownloadListener: MusicFileDownloadListener) {
        val saveDir = FileUtil.getMusicCacheDir(context)

        val total = bos.size
        val countDownLatch = CountDownLatch(total)
        //LogUtil.d("begin download music,size ${bos.size}")
        for (bo in bos) {
            val url = bo.url
            if (url == null || url.isEmpty()) {
                musicFileDownloadListener.onError(Exception("url null"))
                return
            }

            val saveName = bo.md5
            if (saveName == null || saveName.isEmpty()) {
                musicFileDownloadListener.onError(Exception("md5 null"))
                return
            }

            bo.path = saveDir + saveName
            val mission = Mission(url, saveName, saveDir)
            RxDownload.create(mission, true)
                    .subscribe(Consumer<Status> { status ->
                        when (status) {
                            is Succeed -> {
                                musicFileDownloadListener?.onFinishOne(total)
                                countDownLatch.countDown()
                            }
                        }
                    }, Consumer {
                        musicFileDownloadListener.onError(it)
                    })
        }

        countDownLatch.await()
        musicFileDownloadListener?.onSuccess()

        //LogUtil.d("finish download music")
    }

    private fun fillResource2Music(musics: List<Music>, bos: List<MusicResourceBO>) {
        for (music in musics) {
            val bo = bos.find { it.musicId == music.musicId } ?: continue

            music.path = bo.path
        }
    }

    private interface MusicFileDownloadListener {
        fun onFinishOne(total: Int)

        fun onSuccess()

        fun onError(e: Throwable)
    }
}