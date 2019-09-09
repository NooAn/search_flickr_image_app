package com.an.droid.test.model

import org.json.JSONObject

/*
 Class which parse photos from Flickr
 Very fast solution with hardcoded values
 */
interface PhotoJSONParser<out T> {
    fun parsePhotosFromJSONObject(json: String): T
}

class FlickrParser : PhotoJSONParser<Photos> {
    override fun parsePhotosFromJSONObject(json: String): Photos {
        val photos = ArrayList<Photo>()
        val jsonObject = JSONObject(json).optJSONObject("photos")
        val pages = jsonObject?.optInt("pages") ?: 0
        val photoJSONArray = jsonObject?.optJSONArray("photo")
        photoJSONArray?.let {
            for (index in 0 until it.length()) {
                photos.add(
                    parsePhotoFromJSONObject(
                        it.optJSONObject(index)
                    )
                )
            }
        }
        return Photos(photos, pages)

    }

    private fun parsePhotoFromJSONObject(jsonObject: JSONObject) =
        Photo(
            id = jsonObject.optString("id"),
            farm = jsonObject.optInt("farm"),
            secret = jsonObject.optString("secret"),
            server = jsonObject.optString("server"),
            title = jsonObject.optString("title")
        )
}
