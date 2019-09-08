package com.an.droid.test.model

import com.an.droid.network.Const.IMAGE_URL

data class Photo(
    var id: String?,
    var secret: String?,
    var server: String?,
    var farm: Int,
    var title: String?
) {

    val imageURL: String
        get() = String.format(IMAGE_URL,farm.toString(), server ?: "", id ?: "",secret ?: "")

}

data class Photos(
    val photo: List<Photo> = listOf<Photo>(),
    val page: Int = 0
)
