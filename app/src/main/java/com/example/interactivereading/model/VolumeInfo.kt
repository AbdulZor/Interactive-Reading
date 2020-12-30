package com.example.interactivereading.model

import com.google.gson.annotations.SerializedName

data class VolumeInfo(
    @SerializedName("title") val title: String = "No Title",
    @SerializedName("authors") val authors: List<String>,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("publishedDate") val publishedDate: String?,
    @SerializedName("description") val description: String = "No Description",
    @SerializedName("pageCount") val pageCount: Int = 0,
    @SerializedName("mainCategory") val mainCategory: String?,
    @SerializedName("averageRating") val averageRating: Double?,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?,
    @SerializedName("language") val language: String?,
    @SerializedName("infoLink") val infoLink: String?
) {
    fun getAuthors(): String {
        if (authors.isNullOrEmpty()) {
            return "Author(s) unknown"
        }
        return authors.joinToString() // returns (e.g. author 1, author 2)
    }
}