package com.asta.cherrymessages.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.data.repository.DraftRepository
import com.asta.cherrymessages.data.repository.SmsRepository
import com.asta.cherrymessages.ui.navigation.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel @AssistedInject constructor(
    private val smsRepository: SmsRepository,
    private val draftRepository: DraftRepository,
    @Assisted private val screen: Screen.Chat
) : ViewModel() {

    val threadId: Long = screen.threadId
    val address: String = screen.address

    val messages: StateFlow<List<MessageEntity>> = smsRepository.getMessagesForThread(threadId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    init {
        viewModelScope.launch {
            smsRepository.markThreadAsRead(threadId)
            draftRepository.getDraft(threadId).collect { draft ->
                if (draft != null && _messageInput.value.isBlank()) {
                    _messageInput.value = draft.body
                }
            }
        }
    }

    fun updateMessageInput(input: String) {
        _messageInput.value = input
        viewModelScope.launch {
            draftRepository.saveDraft(threadId, input)
        }
    }

    fun sendMessage() {
        val body = _messageInput.value.trim()
        if (body.isBlank()) return

        viewModelScope.launch {
            smsRepository.sendMessage(threadId, address, body)
            _messageInput.value = ""
            draftRepository.deleteDraft(threadId)
        }
    }

    fun toggleStar(messageId: Long, isStarred: Boolean) {
        viewModelScope.launch {
            smsRepository.updateStarStatus(messageId, !isStarred)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(screen: Screen.Chat): ChatViewModel
    }
}
