package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.utils.Constants
import com.udacity.asteroidradar.domain.PictureOfDay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .client(
        OkHttpClient()
            .newBuilder()
            .addInterceptor { chain ->
                val url =
                    chain.request().url().newBuilder()
                        .addQueryParameter("api_key", BuildConfig.NASA_API_KEY)
                        .build()
                chain.proceed(chain.request().newBuilder().url(url).build())
            }
            .build()
    )
    .build()

interface AsteroidsApiService {
    @GET("planetary/apod")
    suspend fun getPictureOfDay(): PictureOfDay

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") fromDate: String,
        @Query("end_date") toDate: String
    ): String
}

object AsteroidsApi {
    val retrofitService: AsteroidsApiService by lazy { retrofit.create(AsteroidsApiService::class.java) }
}
