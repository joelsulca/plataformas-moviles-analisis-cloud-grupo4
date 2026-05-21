package com.masterdog.app.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Punto único de configuración de la base URL y de Retrofit.
 * Toda la app consume `ApiClient.api` (lazy singleton).
 */
object ApiClient {

    const val BASE_URL = "https://jq84uge0m9.execute-api.us-east-2.amazonaws.com/v1/"

    val api: MasterDogApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MasterDogApi::class.java)
    }
}
