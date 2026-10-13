package com.asta.cherrymessages.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.data.repository.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val smsRepository: SmsRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val searchResults: StateFlow<List<MessageEntity>> = _query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList()) else smsRepository.searchMessages(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }
}
