package com.an.droid.test.presenter

import androidx.fragment.app.FragmentActivity
import com.an.droid.network.BuildConfig
import com.an.droid.network.Const.FLICKR_URL
import com.an.droid.network.Network
import com.an.droid.test.model.PhotoJSONParser
import com.an.droid.test.model.Photos
import com.an.droid.test.takePhotos
import com.an.droid.test.view.MainView
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future

interface Presenter {
    fun search(query: String?, isNextPage: Boolean = false)
    fun stop()
}

class SearchPresenter(
    private val view: MainView,
    private val api: Network,
    private val mainThreadExecutor: Executor,
private val parser: PhotoJSONParser<Photos>
) : Presenter {
    private var currentQuery = ""
    private var currentPage = 1
    private val executorService = Executors.newCachedThreadPool()
    private var future: Future<*>? = null
    private var refreshing = false
    private fun io(runnable: () -> Unit) = executorService.submit(runnable)

    private fun mainThread(runnable: () -> Unit) = mainThreadExecutor.execute(runnable)

    override fun search(query: String?, isNextPage: Boolean) {
        if (!refreshing) {
            refreshing = true
            currentPage = if (isNextPage) currentPage + 1 else 0
            if (query != null) {
                currentQuery = query
            }

           view.showProgress()

            future = io {
                try {
                    val photos = api.request(getUrl()).takePhotos(parser)
                    mainThread {
                        view.addItems(photos)
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                    }
                    mainThread {
                        view.showError()
                    }
                } finally {
                    mainThread {
                        view.hideProgress()
                    }
                    refreshing = false
                }
            }
        }
    }

    private fun getUrl(): String {
        return FLICKR_URL.plus(currentQuery).plus("&page=$currentPage")
    }

    override fun stop() {
        if (future?.isCancelled == false)
            future?.cancel(false)
    }
}


class MainThreadExecutor(private val activity: FragmentActivity) : Executor {
    override fun execute(command: Runnable) {
        if (activity.isFinishing) return
        activity.runOnUiThread(command)
    }
}


