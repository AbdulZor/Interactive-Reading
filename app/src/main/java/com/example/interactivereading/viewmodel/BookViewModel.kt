package com.example.interactivereading.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interactivereading.model.BookResult
import com.example.interactivereading.repository.BookRepository
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {

    private val bookRepository: BookRepository = BookRepository()

    val books: LiveData<BookResult> = bookRepository.books

    fun getBooks(searchText: String) {
        if (searchText.isNotBlank()) {
            viewModelScope.launch {
                // Invoke getBooks, then books LiveData object will be updated
                // Observing views will get notified
                bookRepository.getBooks(searchText)
            }
        } else {
            return
        }
    }

}