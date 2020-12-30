package com.example.interactivereading.model

import com.google.gson.annotations.SerializedName

data class VolumeInfo(
    @SerializedName("title") val title: String = "No Title",
    @SerializedName("authors") val authors: List<String>,
    @SerializedName("publishedDate") val publishedDate: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("pageCount") val pageCount: Int = 0,
    @SerializedName("averageRating") val averageRating: Double?,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?,
    @SerializedName("language") val language: String?,
) {
    fun getAuthors(): String {
        if (authors.isNullOrEmpty()) {
            return "Author(s) unknown"
        }
        // returns (e.g. author 1, author 2), we add an extra space at the end so that the text is not cut on the edges because of the italic font style
        return authors.joinToString() + " "
    }
}