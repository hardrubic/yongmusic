package com.hardrubic.music.biz.resource

import android.content.Context
import com.hardrubic.music.R
import com.hardrubic.music.biz.interf.MusicResourceListener
import com.hardrubic.music.entity.bo.MusicRelatedBO
import com.hardrubic.music.entity.bo.MusicResourceBO
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.util.file.FilePathUtil
import com.hardrubic.music.util.file.FileUtils
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Mission
import zlc.season.rxdownload3.core.Status
import zlc.season.rxdownload3.core.Succeed
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
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
            var musicRelatedBOs = mutableListOf<MusicRelatedBO>()
            var musicResourceBOs = mutableListOf<MusicResourceBO>()

            //step1 resource info
            var countDownLatch = CountDownLatch(1)
            httpService.applyMusicResource(musicIds)
                    .subscribe(Consumer {
                        musicResourceBOs = it.toMutableList()
                        val failMusicIds = verifyResource(context, musicResourceBOs)
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
            countDownLatch = CountDownLatch(musicIds.size)
            val schedulers = if (musicIds.size == 1) {
                Schedulers.io()
            } else {
                Schedulers.from(Executors.newFixedThreadPool(8))
            }
            for (index in 0 until musicIds.size) {
                httpService.applyMusicDetail(musicIds[index], schedulers)
                        .subscribe(Consumer {
                            musicRelatedBOs.add(it)

                            refreshProgress(ProgressWeight.MUSIC_DETAIL.toFloat())
                            countDownLatch.countDown()
                        }, Consumer {
                            musicResourceListener?.onError(it)
                        })
            }
            countDownLatch.await()

            //step3 download file
            val downloadResources = filterDownloadResource(musicResourceBOs)
            downloadFileSync(downloadResources, musicFileDownloadListener)

            //step4 build related
            fillResourcePath2Music(musicRelatedBOs, musicResourceBOs)

            musicResourceListener?.onSuccess(musicRelatedBOs)
        }
    }

    private fun verifyResource(context: Context, resources: MutableList<MusicResourceBO>): List<Long> {
        //filter invalid resource
        val failMusicIds = mutableListOf<Long>()
        val iterator = resources.iterator()
        while (iterator.hasNext()) {
            val resource = iterator.next()
            if ((resource.url == null || resource.url!!.isEmpty()) && (resource.md5 == null || resource.md5!!.isEmpty())) {
                failMusicIds.add(resource.musicId)
                iterator.remove()
            }
        }

        //fill resource info
        val saveDir = FilePathUtil.getMusicCacheDir(context)
        for (resource in resources) {
            resource.dir = saveDir
            resource.saveName = resource.md5!!
        }

        return failMusicIds
    }

    private fun filterDownloadResource(resources: List<MusicResourceBO>): List<MusicResourceBO> {
        val downloadResources = mutableListOf<MusicResourceBO>()
        for (resource in resources) {
            val path = resource.getPath()
            if (!FileUtils.isFileExist(path)) {
                downloadResources.add(resource)
            }
        }
        return downloadResources
    }

    private fun downloadFileSync(bos: List<MusicResourceBO>, musicFileDownloadListener: MusicFileDownloadListener) {
        if (bos.isEmpty()) {
            return
        }

        val total = bos.size
        val countDownLatch = CountDownLatch(total)
        //LogUtil.d("begin download music,size ${bos.size}")
        for (bo in bos) {
            val url = bo.url!!

            val mission = Mission(url, bo.saveName, bo.dir)
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
    }

    private fun fillResourcePath2Music(relatedBOs: List<MusicRelatedBO>, resourceBOs: List<MusicResourceBO>) {
        for (relatedBO in relatedBOs) {
            val bo = resourceBOs.find { it.musicId == relatedBO.music.musicId } ?: continue

            relatedBO.music.path = bo.getPath()
        }
    }

    private interface MusicFileDownloadListener {
        fun onFinishOne(total: Int)

        fun onSuccess()

        fun onError(e: Throwable)
    }
}