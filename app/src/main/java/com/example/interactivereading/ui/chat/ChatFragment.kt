package com.example.interactivereading.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interactivereading.R
import com.example.interactivereading.databinding.FragmentChatBinding
import com.example.interactivereading.model.ChatMessage
import com.example.interactivereading.viewmodel.BooksSharedViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val sharedViewModel: BooksSharedViewModel by activityViewModels()
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
                val messageReference = firebaseDatabase.child(this.bookId.toString())

                val fireBaseRecyclerOptions = FirebaseRecyclerOptions.Builder<ChatMessage>()
                    .setQuery(messageReference, ChatMessage::class.java).setLifecycleOwner(this).build()
                chatAdapter = ChatAdapter(fireBaseRecyclerOptions)
                binding.rvMessageList.setHasFixedSize(true)
                binding.rvMessageList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                binding.rvMessageList.adapter = chatAdapter
            }
        }
        )
        binding.btnChatboxSend.setOnClickListener {
            if (!binding.etChatbox.text.isNullOrBlank()) {
                sendMessage()
            }
        }
        return binding.root
    }

    private fun sendMessage() {
        val chatMessage = ChatMessage(
            binding.etChatbox.text.toString(),
            auth.currentUser?.email ?: getString(R.string.anonymous)
        )
        binding.etChatbox.text.clear()

        firebaseDatabase.child(this.bookId.toString()).push()
            .setValue(chatMessage).addOnSuccessListener {

            }.addOnFailureListener {
                Toast.makeText(context, getString(R.string.error_sending_message), Toast.LENGTH_LONG).show()
            }
    }
}