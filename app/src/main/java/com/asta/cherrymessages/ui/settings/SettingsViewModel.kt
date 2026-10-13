package com.asta.cherrymessages.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymessages.data.local.entity.BlockedNumberEntity
import com.asta.cherrymessages.data.repository.BlockedNumberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val blockedNumberRepository: BlockedNumberRepository
) : ViewModel() {

    val blockedNumbers: StateFlow<List<BlockedNumberEntity>> = blockedNumberRepository.getBlockedNumbers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _fontSize = MutableStateFlow(16)
    val fontSize: StateFlow<Int> = _fontSize.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun updateFontSize(size: Int) {
        _fontSize.value = size
    }

    fun blockNumber(phoneNumber: String) {
        val digits = phoneNumber.filter { it.isDigit() }
        if (digits.isBlank()) return
        val normalized = "+$digits"
        viewModelScope.launch {
            blockedNumberRepository.blockNumber(normalized)
        }
    }

    fun unblockNumber(phoneNumber: String) {
        viewModelScope.launch {
            blockedNumberRepository.unblockNumber(phoneNumber)
        }
    }
}
