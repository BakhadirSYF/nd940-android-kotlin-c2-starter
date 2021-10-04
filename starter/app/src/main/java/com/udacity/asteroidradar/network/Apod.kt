package com.udacity.asteroidradar.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Astronomy Picture of the Day network model
 */
@Parcelize
data class Apod(
    val title: String,
    @Json(name = "media_type") val mediaType: String,
    @Json(name = "url") val imgSrcUrl: String
) : Parcelable