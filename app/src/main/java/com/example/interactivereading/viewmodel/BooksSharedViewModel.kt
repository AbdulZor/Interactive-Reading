package com.example.interactivereading.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.interactivereading.model.Book

class BooksSharedViewModel : ViewModel() {
    private var _selectedBook: MutableLiveData<Book> = MutableLiveData()

    val selectedBook: LiveData<Book>
        get() = _selectedBook

    fun selectBook(book: Book) {
        _selectedBook.value = book
    }
}