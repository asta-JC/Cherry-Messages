package com.asta.cherrymessages.ui.newmessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymessages.data.repository.SmsRepository
import com.asta.cherrymessages.util.normalizePhoneNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewMessageViewModel @Inject constructor(
    private val smsRepository: SmsRepository
) : ViewModel() {

    private val _recipient = MutableStateFlow("")
    val recipient: StateFlow<String> = _recipient.asStateFlow()

    private val _messageBody = MutableStateFlow("")
    val messageBody: StateFlow<String> = _messageBody.asStateFlow()

    fun updateRecipient(newRecipient: String) {
        _recipient.value = newRecipient.filter { it.isDigit() }
    }

    fun updateMessageBody(newBody: String) {
        _messageBody.value = newBody
    }

    fun sendNewMessage(onSuccess: (Long, String) -> Unit) {
        val rawInput = _recipient.value.trim()
        val body = _messageBody.value.trim()
        if (rawInput.isBlank() || body.isBlank()) return

        val cleanAddress = normalizePhoneNumber(rawInput)
        if (cleanAddress.isBlank()) return

        viewModelScope.launch {
            val threadId = smsRepository.getOrCreateThreadId(cleanAddress)
            smsRepository.sendMessage(threadId, cleanAddress, body)
            onSuccess(threadId, cleanAddress)
        }
    }
}
