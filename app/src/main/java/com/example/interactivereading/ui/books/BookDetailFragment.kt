package com.example.interactivereading.ui.books

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.interactivereading.R
import com.example.interactivereading.databinding.FragmentBookDetailBinding
import com.example.interactivereading.viewmodel.BooksSharedViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BookDetailFragment : Fragment() {
    private lateinit var binding: FragmentBookDetailBinding
    private val sharedViewModel: BooksSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChat.setOnClickListener {
            findNavController().navigate(R.id.action_BookDetailFragment_to_ChatFragment)
        }

        observeBook()
    }

    private fun observeBook() {
        sharedViewModel.selectedBook.observe(viewLifecycleOwner, { book ->
            Glide.with(this)
                .load(book.volumeInfo.imageLinks?.smallThumbnail)
                .placeholder(R.drawable.ic_no_thumnail)
                .into(binding.ivPosterImage)

            binding.tvBookTitle.text = book.volumeInfo.title
            binding.tvBookAuthors.text = book.volumeInfo.getAuthors()
            binding.tvBookPublishDate.text =
                getString(R.string.published_date, book.volumeInfo.publishedDate ?: "")
            binding.tvBookPages.text =
                getString(R.string.pages, book.volumeInfo.pageCount.toString())
            binding.tvBookLanguage.text = getString(R.string.language, book.volumeInfo.language)

            binding.tvDescriptionText.text = book.volumeInfo.description
            binding.rbStarRating.rating = book.volumeInfo.averageRating?.toFloat() ?: 0f
        })
    }
}