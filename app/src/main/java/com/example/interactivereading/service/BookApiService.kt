package com.example.interactivereading.service

import com.example.interactivereading.model.BookResult
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApiService {

    @GET("volumes")
    suspend fun getBooks(
        @Query("q") searchText: String,
        @Query("maxResults") maxResults: Int = DEFAULT_RESULTS_BOOKS_AMOUNT
    ): BookResult

    companion object {
        private const val DEFAULT_RESULTS_BOOKS_AMOUNT: Int = 30
    }
}