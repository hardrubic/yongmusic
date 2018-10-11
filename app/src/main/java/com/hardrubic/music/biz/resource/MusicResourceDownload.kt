package com.hardrubic.music.biz.resource

import android.content.Context
import com.hardrubic.music.R
import com.hardrubic.music.biz.interf.MusicResourceListener
import com.hardrubic.music.entity.bo.MusicRelatedBO
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
            var musicRelatedBOs = listOf<MusicRelatedBO>()
            var musicResourceBOs = mutableListOf<MusicResourceBO>()

            //step1 resource info
            var countDownLatch = CountDownLatch(1)
            httpService.applyMusicResource(musicIds)
                    .subscribe(Consumer {
                        musicResourceBOs = it.toMutableList()
                        val failMusicIds = verifyResource(musicResourceBOs)
                        if (failMusicIds.size == it.size) {
                            musicFileDownloadListener.onError(Exception("${context.getString(R.string.hint_download_fail)}"))
                            return@Consumer
                        }

                        refreshProgress(ProgressWeight.MUSIC_RESOURCE.toFloat())
                        countDownLatch.countDown()
                    }, Consumer {
                        musicResourceListener?.onError(it)
                    })
            countDownLatch.await()

            //step2 music detail
            countDownLatch = CountDownLatch(1)
            val resourceOkMusicIds = musicResourceBOs.map { it.musicId }
            httpService.applyMusicDetail(resourceOkMusicIds)
                    .subscribe(Consumer {
                        musicRelatedBOs = it

                        refreshProgress(ProgressWeight.MUSIC_DETAIL.toFloat())
                        countDownLatch.countDown()
                    }, Consumer {
                        musicResourceListener?.onError(it)
                    })
            countDownLatch.await()

            //step3 music file
            downloadFileSync(context, musicResourceBOs, musicFileDownloadListener)
            fillResource2Music(musicRelatedBOs, musicResourceBOs)
            musicResourceListener?.onSuccess(musicRelatedBOs)
        }
    }

    private fun verifyResource(list: MutableList<MusicResourceBO>): List<Long> {
        val failMusicIds = mutableListOf<Long>()
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val entity = iterator.next()
            if ((entity.url == null || entity.url!!.isEmpty()) && (entity.md5 == null || entity.md5!!.isEmpty())) {
                failMusicIds.add(entity.musicId)
                iterator.remove()
            }
        }
        return failMusicIds
    }

    private fun downloadFileSync(context: Context, bos: List<MusicResourceBO>, musicFileDownloadListener: MusicFileDownloadListener) {
        val saveDir = FileUtil.getMusicCacheDir(context)

        val total = bos.size
        val countDownLatch = CountDownLatch(total)
        //LogUtil.d("begin download music,size ${bos.size}")
        for (bo in bos) {
            val url = bo.url!!
            val saveName = bo.md5!!

            bo.path = saveDir + saveName
            val mission = Mission(url, saveName, saveDir)
            RxDownload.create(mission, true)
                    .subscribe(Consumer<Status> { status ->
                        when (status) {
                            is Succeed -> {
                                musicFileDownloadListener.onFinishOne(total)
                                countDownLatch.countDown()
                            }
                        }
                    }, Consumer {
                        musicFileDownloadListener.onError(it)
                    })
        }

        countDownLatch.await()
        musicFileDownloadListener.onSuccess()

        //LogUtil.d("finish download music")
    }

    private fun fillResource2Music(relatedBOs: List<MusicRelatedBO>, resourceBOs: List<MusicResourceBO>) {
        for (relatedBO in relatedBOs) {
            val bo = resourceBOs.find { it.musicId == relatedBO.music.musicId } ?: continue

            relatedBO.music.path = bo.path
        }
    }

    private interface MusicFileDownloadListener {
        fun onFinishOne(total: Int)

        fun onSuccess()

        fun onError(e: Throwable)
    }
}