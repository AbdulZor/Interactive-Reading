package com.example.interactivereading.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.interactivereading.R
import com.example.interactivereading.exceptions.ChatMessagePushError
import com.example.interactivereading.model.ChatMessage
import com.example.interactivereading.repository.ChatRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val chatRepository = ChatRepository()

    private val _errorText: MutableLiveData<String> = MutableLiveData()
    val errorText: LiveData<String>
        get() = _errorText

    fun pushMessage(message: String, userMail: String, bookId: String) {
        val chatMessage = ChatMessage(message, userMail)

        viewModelScope.launch {
            try {
                chatRepository.pushMessage(chatMessage, bookId)
            } catch (ex: ChatMessagePushError) {
                val errorMsg = R.string.error_sending_message.toString()
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun createFireBaseRecyclerOptions(
        viewLifecycleOwner: LifecycleOwner,
        bookId: String
    ): FirebaseRecyclerOptions<ChatMessage> {
        val messageReference = chatRepository.getFirebaseChatMessageReference(bookId)
        return FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(messageReference, ChatMessage::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }

}