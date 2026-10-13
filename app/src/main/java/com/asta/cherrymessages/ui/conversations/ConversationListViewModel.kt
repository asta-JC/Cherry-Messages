package com.asta.cherrymessages.ui.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymessages.data.local.entity.ConversationEntity
import com.asta.cherrymessages.data.repository.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val smsRepository: SmsRepository
) : ViewModel() {

    val conversations: StateFlow<List<ConversationEntity>> = smsRepository.getAllConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteConversation(threadId: Long) {
        viewModelScope.launch {
            smsRepository.deleteThread(threadId)
        }
    }

    fun togglePin(threadId: Long, isPinned: Boolean) {
        viewModelScope.launch {
            smsRepository.updatePinStatus(threadId, !isPinned)
        }
    }

    fun toggleArchive(threadId: Long, isArchived: Boolean) {
        viewModelScope.launch {
            smsRepository.updateArchiveStatus(threadId, !isArchived)
        }
    }
}
