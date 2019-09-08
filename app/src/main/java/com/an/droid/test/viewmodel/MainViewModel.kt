package com.an.droid.test.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.an.droid.network.ApiNetwork
import com.an.droid.network.Const.FLICKR_URL
import com.an.droid.test.model.Parser
import com.an.droid.test.model.Result
import com.an.droid.test.model.Photos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import android.app.Application
import androidx.lifecycle.ViewModelProvider



class MainViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    val photos = MutableLiveData<Result<Photos>>()

    private val repository = ApiNetwork() // DI
    private var currentQuery = ""
    private var currentPage = 1

    fun search(query: String?, isNextPage: Boolean = false) {
        currentPage += if(isNextPage) 1 else 0
        if (query != null) {
            currentQuery = query
        }
        launch {
            var result: Result<Photos>?
            photos.postValue(Result.Loading(true))
            try {
                result = Result.Success(repository.request(getUrl()).takePhotos())
            } catch (e: Exception) {
                result = Result.Error(e)
            }
            photos.postValue(Result.Loading(false))
            photos.postValue(result)
        }
    }

    private fun getUrl(): String {
        return FLICKR_URL.plus(currentQuery).plus("&page=$currentPage")
    }
}

private fun String.takePhotos(): Photos {
    return Parser.parsePhotosFromJSONObject(JSONObject(this))
}


