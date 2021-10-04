package com.udacity.asteroidradar.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

private const val BASE_URL = "https://api.nasa.gov/"
const val API_KEY = "your_api_key"

/**
 * OkHttp client with interceptor, which adds "api_key" QueryParameter to each request
 */
private val client: OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor {
        val originalRequest = it.request()
        val originalHttpUrl = originalRequest.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .build()

        val request = originalRequest.newBuilder().url(url).build()

        it.proceed(request)
    }
}.build()

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Build retrofit object using Scalars converter
 */
private val retrofit =
    Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

/**
 * Retrofit service to fetch asteroid list from NASA Api
 */
interface NasaApiService {
    @GET("neo/rest/v1/feed")
    fun getAsteroidsAsync(@QueryMap type: Map<String, String>): Deferred<String>

    @GET("planetary/apod")
    suspend fun getApod(@Query("date") type: String): Apod
}

/**
 * A public Api object that exposes the Retrofit service
 */
object NasaApi {
    val retrofitService: NasaApiService = retrofit.create(NasaApiService::class.java)
}
