package com.example.interactivereading.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.interactivereading.model.BookResult
import com.example.interactivereading.service.BookApi

class BookRepository {
    private val bookApiService = BookApi.createApi()

    private val _books: MutableLiveData<BookResult> = MutableLiveData()

    val books: LiveData<BookResult>
        get() = _books

    suspend fun getBooks(searchText: String) {
        val correctUriSearchText = searchText.split(" ").joinToString("+")
        val booksList = bookApiService.getBooks(correctUriSearchText)
        _books.value = booksList
    }
}