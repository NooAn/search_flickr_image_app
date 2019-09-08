package com.an.droid.network


object Const {
    const val API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736"
    const val FLICKR_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=$API_KEY&format=json&nojsoncallback=1&safe_search=1&text="
    const val IMAGE_URL = "https://farm%s.static.flickr.com/%s/%s_%s.jpg"
}