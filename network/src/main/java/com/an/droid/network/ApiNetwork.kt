package com.an.droid.network

import android.util.Log
import com.an.droid.network.ApiNetwork.ApiConstants.CONNECTION_TIMEOUT
import com.an.droid.network.ApiNetwork.ApiConstants.READ_TIMEOUT
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

interface Network {
    fun request(url: String): String
}

class ApiNetwork() : Network {

    private fun makeConnection(url: String): HttpURLConnection {
        return (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECTION_TIMEOUT
            readTimeout = READ_TIMEOUT
            requestMethod = "GET"
            connect()
        }
    }

    override fun request(url: String): String {
        val httpConnection = makeConnection(url)

        if (httpConnection.responseCode == HttpURLConnection.HTTP_OK) {

            val response = httpConnection.inputStream.bufferedReader().use(BufferedReader::readText)
            if (BuildConfig.DEBUG)
                Log.d("response", response)

            return response
        }
        return ""
    }

    object ApiConstants {
        const val CONNECTION_TIMEOUT = 30 * 1000
        const val READ_TIMEOUT = 30 * 1000
    }
}