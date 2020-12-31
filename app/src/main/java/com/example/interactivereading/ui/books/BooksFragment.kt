package com.example.interactivereading.ui.books

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interactivereading.R
import com.example.interactivereading.databinding.FragmentBooksBinding
import com.example.interactivereading.model.Book
import com.example.interactivereading.viewmodel.BookViewModel
import com.example.interactivereading.viewmodel.BooksSharedViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class BooksFragment : Fragment() {
    private val bookViewModel: BookViewModel by viewModels()
    private val booksSharedViewModel: BooksSharedViewModel by activityViewModels()
    private lateinit var binding: FragmentBooksBinding

    private val books = arrayListOf<Book>()
    private lateinit var booksAdapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBooksBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        booksAdapter = BooksAdapter(books, ::onBookClick)
        binding.rvBooks.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvBooks.adapter = booksAdapter
        binding.rvBooks.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

        // observe the LiveData books object to update the RecyclerView when changes happened
        observeBooks()

        // Default books
        if (books.isEmpty()) {
            getBooks("Harry Potter")
        }
    }

    private fun observeBooks() {
        bookViewModel.books.observe(viewLifecycleOwner, {
            binding.pbLoadingBooks.visibility = View.INVISIBLE
            this.books.clear()
            this.books.addAll(it.books)
            this.booksAdapter.notifyDataSetChanged()
        })
    }

    private fun onBookClick(book: Book) {
        booksSharedViewModel.selectBook(book)
        findNavController().navigate(R.id.action_BooksFragment_to_BookDetailFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.search_action).isVisible = true

        // Get the SearchView and set the searchable configuration
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.search_action)
        val searchView = searchItem.actionView as SearchView
        searchView.setIconifiedByDefault(false) // Do not iconify the widget; expand it by default

        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // If search is clicked reset the SearchView input
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()

                if (!query.isNullOrBlank()) {
                    getBooks(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Gets the books and informs the user of retrieval process
     */
    private fun getBooks(searchText: String) {
        books.clear()
        binding.pbLoadingBooks.visibility = View.VISIBLE
        bookViewModel.getBooks(searchText)
    }
}