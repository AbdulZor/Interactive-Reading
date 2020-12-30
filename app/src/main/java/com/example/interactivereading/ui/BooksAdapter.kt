package com.example.interactivereading.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.interactivereading.R
import com.example.interactivereading.databinding.ItemBookBinding
import com.example.interactivereading.model.Book

class BooksAdapter(private val books: List<Book>, private val onClick: (Book) -> Unit) :
    RecyclerView.Adapter<BooksAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemBookBinding = ItemBookBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(itemBookBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int {
        return books.size
    }

    inner class ViewHolder(private val itemBook: ItemBookBinding) :
        RecyclerView.ViewHolder(itemBook.root) {

        init {
            itemBook.root.setOnClickListener {
                onClick(books[adapterPosition])
            }
        }

        fun bind(book: Book) {
            Glide.with(context)
                .load(book.volumeInfo.imageLinks?.smallThumbnail)
                .placeholder(R.drawable.ic_no_thumnail)
                .into(itemBook.ivBookSmallThumbnail)

            itemBook.tvBookTitle.text = book.volumeInfo.title
            itemBook.tvBookAuthors.text = book.volumeInfo.getAuthors()
            itemBook.tvBookDescription.text = book.volumeInfo.description
            itemBook.tvBookPages.text =
                context.getString(R.string.pages, book.volumeInfo.pageCount.toString())
        }
    }
}