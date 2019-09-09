package com.an.droid.test

import com.an.droid.network.ApiNetwork
import com.an.droid.network.Network
import com.an.droid.test.model.FlickrParser
import com.an.droid.test.view.MainFragment
import com.an.droid.test.presenter.MainThreadExecutor
import com.an.droid.test.presenter.Presenter
import com.an.droid.test.presenter.SearchPresenter

/**
 * Class Dependency for so simple implementation dependency injection
 * I'd rather use Koin or Dagger2 in my project with library :)
 */
object Dependency {
    fun inject(fragment: MainFragment): Presenter {
        return SearchPresenter(fragment, api(), MainThreadExecutor(fragment.activity!!), parser)
    }

    private val api: () -> Network = { ApiNetwork() }
    private val parser = FlickrParser()
}