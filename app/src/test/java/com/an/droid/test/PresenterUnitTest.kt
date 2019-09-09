package com.an.droid.test

import com.an.droid.network.Network
import com.an.droid.test.model.Photo
import com.an.droid.test.model.PhotoJSONParser
import com.an.droid.test.model.Photos
import com.an.droid.test.presenter.Presenter
import com.an.droid.test.presenter.SearchPresenter
import com.an.droid.test.view.MainView
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PresenterUnitTest {
    @Mock
    private lateinit var view: MainView
    @Mock
    private lateinit var api: Network
    @Mock
    private lateinit var executor: Executor
    @Mock
    private lateinit var parser: PhotoJSONParser<Photos>

    private lateinit var presenter: Presenter

    private lateinit var photos: Photos


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        photos = Photos(
            arrayListOf(
                Photo(
                    id = "1",
                    title = "Tesla",
                    secret = "!!123",
                    farm = 66,
                    server = "90"
                ),
                Photo(
                    id = "2",
                    title = "Tesla2",
                    secret = "!!223",
                    farm = 1,
                    server = "90"
                )
            ), 1
        )
        given(executor.execute(any())).will { it.getArgument<Runnable>(0).run() }
        given(parser.parsePhotosFromJSONObject(jsonTesla)).willReturn(photos)
        presenter = SearchPresenter(view, api, executor, parser)

    }
    @Test
    fun photoUrl_should_be_correct() {
        assertEquals("https://farm66.static.flickr.com/90/1_!!123.jpg",photos.photo[0].imageURL)
    }

    @Test
    fun presenter_should_load_more() {
        `when`(api.request(any())).thenReturn(jsonTesla)
        presenter.search("test",true)
        verify(view).addItems(eq(photos))
    }

    @Test
    fun presenter_not_should_get_error() {
        `when`(api.request(any())).thenReturn(jsonTesla)
        presenter.search("test",false)
        verify(view, never()).showError()
    }

}
