package com.example.interactivereading.model

import com.google.gson.annotations.SerializedName

data class BookResult(
    @SerializedName("items") val books: List<Book>
)