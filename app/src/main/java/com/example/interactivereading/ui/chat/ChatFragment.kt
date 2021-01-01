package com.example.interactivereading.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interactivereading.R
import com.example.interactivereading.databinding.FragmentChatBinding
import com.example.interactivereading.exceptions.ChatMessagePushError
import com.example.interactivereading.model.ChatMessage
import com.example.interactivereading.viewmodel.BooksSharedViewModel
import com.example.interactivereading.viewmodel.ChatViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val sharedViewModel: BooksSharedViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var chatAdapter: ChatAdapter

    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firebaseDatabase = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater)

        sharedViewModel.selectedBook.observe(viewLifecycleOwner, { book ->
            binding.pbLoadingBooks.visibility = View.INVISIBLE
            this.bookId = book.id

            if (this.bookId != null) {
                binding.rvMessageList.setHasFixedSize(true)
                binding.rvMessageList.layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)

                // Setup adapter
                val fireBaseRecyclerOptions =
                    chatViewModel.createFireBaseRecyclerOptions(this, bookId.toString())
                chatAdapter = ChatAdapter(fireBaseRecyclerOptions)
                chatAdapter.registerAdapterDataObserver(object :
                    RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        val messageCount = chatAdapter.itemCount
                        val lastVisiblePosition =
                            (binding.rvMessageList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        // If the recycler view is initially being loaded or the
                        // user is at the bottom of the list, scroll to the bottom
                        // of the list to show the newly added message.
                        if (lastVisiblePosition == -1 ||
                            (positionStart >= (messageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))
                        ) {
                            binding.rvMessageList.scrollToPosition(positionStart);
                        }
                    }
                })
                binding.rvMessageList.adapter = chatAdapter
            }
        }
        )
        binding.btnChatboxSend.setOnClickListener {
            if (!binding.etChatbox.text.isNullOrBlank()) {
                sendMessage()
            }
        }
        observeChat()
        return binding.root
    }

    private fun observeChat() {
        chatViewModel.errorText.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun sendMessage() {
        chatViewModel.pushMessage(
            binding.etChatbox.text.toString(),
            auth.currentUser?.email ?: getString(R.string.anonymous), this.bookId.toString()
        )
        binding.etChatbox.text.clear()

    }
}