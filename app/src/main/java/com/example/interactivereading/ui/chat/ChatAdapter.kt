package com.example.interactivereading.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.interactivereading.databinding.ItemChatBinding
import com.example.interactivereading.model.ChatMessage
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ChatAdapter(options: FirebaseRecyclerOptions<ChatMessage>) :
    FirebaseRecyclerAdapter<ChatMessage, ChatAdapter.ViewHolder>(options) {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemChatBinding = ItemChatBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(itemChatBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatMessage) {
        holder.bind(model)
    }

    inner class ViewHolder(private val itemChat: ItemChatBinding) :
        RecyclerView.ViewHolder(itemChat.root) {

        fun bind(chatMessage: ChatMessage) {
            itemChat.tvMessage.text = chatMessage.textMessage
            itemChat.tvMessageSenderName.text = chatMessage.name
        }
    }
}