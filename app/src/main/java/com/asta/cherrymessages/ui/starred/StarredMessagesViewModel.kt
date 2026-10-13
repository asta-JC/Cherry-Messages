package com.asta.cherrymessages.ui.starred

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.data.repository.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StarredMessagesViewModel @Inject constructor(
    private val smsRepository: SmsRepository
) : ViewModel() {

    val starredMessages: StateFlow<List<MessageEntity>> = smsRepository.getStarredMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
