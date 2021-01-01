package com.example.interactivereading.repository

import com.example.interactivereading.exceptions.ChatMessagePushError
import com.example.interactivereading.model.ChatMessage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class ChatRepository {
    private val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun pushMessage(chatMessage: ChatMessage, bookId: String) {
        firebaseDatabase.child(bookId).push()
            .setValue(chatMessage)
            .addOnFailureListener {
                throw ChatMessagePushError(it.message.toString())
            }
    }

    fun getFirebaseChatMessageReference(bookId: String): Query {
        return firebaseDatabase.child(bookId)
    }
}