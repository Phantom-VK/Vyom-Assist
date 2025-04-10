package com.swag.vyom.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {
    private const val BASE_URL = "https://sggsapp.co.in/vyom/"
    private const val FACE_AUTH_BASE_URL = "https://deepfaceapi-847981499984.asia-south1.run.app"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Custom OkHttpClient for face authentication with longer timeouts
    private val faceAuthHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Connection", "keep-alive")
                .build()
            chain.proceed(request)
        }
        .build()

    // Regular client for other API calls
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    val faceAuthInstance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(FACE_AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(faceAuthHttpClient) // Use the custom client with longer timeouts
            .build()
            .create(ApiService::class.java)
    }
}


