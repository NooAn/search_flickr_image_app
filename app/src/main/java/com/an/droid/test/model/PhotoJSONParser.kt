package com.an.droid.test.model

import org.json.JSONObject
import java.lang.Exception

/*
 Class which parse photos from Flickr
 */
class Parser {
    companion object {
        fun parsePhotosFromJSONObject(json: JSONObject): Photos {
            val photos = ArrayList<Photo>()
            try {
                val jsonObject = json.optJSONObject("photos")
                val pages = jsonObject?.optInt("pages") ?: 0
                val photoJSONArray = jsonObject?.optJSONArray("photo")
                photoJSONArray?.let {
                    for (index in 0 until it.length()) {
                        val p = it.optJSONObject(index)
                        photos.add(
                            parsePhotoFromJSONObject(
                                p
                            )
                        )
                    }
                }
                return Photos(photos, pages)
            } catch (e: Exception) {
                return Photos(photos, 0)
            }
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
}
