package com.an.droid.test.model

import android.os.Parcelable
import com.an.droid.network.Const.IMAGE_URL
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    var id: String?,
    var secret: String?,
    var server: String?,
    var farm: Int,
    var title: String?
) : Parcelable {

    val imageURL: String
        get() = String.format(IMAGE_URL,farm.toString(), server ?: "", id ?: "",secret ?: "")

}

@Parcelize
data class Photos(
    val photo: List<Photo> = listOf<Photo>(),
    val page: Int = 0
) : Parcelable
