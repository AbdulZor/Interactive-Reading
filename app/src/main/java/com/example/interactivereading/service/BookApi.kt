package com.example.interactivereading.service

import com.example.interactivereading.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookApi {
    companion object {
        private const val BASE_URL = "https://www.googleapis.com/books/v1/"

        fun createApi(): BookApiService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor { addApiKeyInterceptor(it) }
                .build()

            val movieApi = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return movieApi.create(BookApiService::class.java)
        }

        private fun addApiKeyInterceptor(it: Interceptor.Chain): Response {
            val originalRequest = it.request()
            val originalHttpUrl = originalRequest.url

            val urlWithApiKey = originalHttpUrl.newBuilder()
                .addQueryParameter("key", BuildConfig.GOOGLE_BOOKS_API_KEY)
                .build()

            val requestWithApiKey = originalRequest.newBuilder()
                .url(urlWithApiKey)
                .build()

            return it.proceed(request = requestWithApiKey)
        }
    }
}