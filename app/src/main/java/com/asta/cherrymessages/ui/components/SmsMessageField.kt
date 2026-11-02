package com.asta.cherrymessages.ui.components

import android.telephony.SmsMessage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

const val MAX_SMS_PARTS = 10

data class SmsLength(val characters: Int, val parts: Int, val remaining: Int)

fun smsLength(text: String): SmsLength {
    val length = SmsMessage.calculateLength(text, false)
    return SmsLength(text.codePointCount(0, text.length), length[0].coerceAtLeast(1), length[2])
}

@Composable
fun SmsMessageField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, maxLines: Int = 5) {
    val length = remember(value) { smsLength(value) }
    var limitReached by rememberSaveable { mutableStateOf(false) }
    val overLimit = length.parts > MAX_SMS_PARTS
    OutlinedTextField(
        value = value,
        onValueChange = { candidate ->
            val candidateLength = smsLength(candidate)
            if (candidateLength.parts <= MAX_SMS_PARTS || candidate.length < value.length) {
                limitReached = candidateLength.parts > MAX_SMS_PARTS
                onValueChange(candidate)
            } else limitReached = true
        },
        modifier = modifier,
        placeholder = { Text("Type a message…") },
        shape = RoundedCornerShape(20.dp),
        maxLines = maxLines,
        isError = limitReached || overLimit,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        supportingText = {
            Column(Modifier.fillMaxWidth()) {
                Text("${length.characters} characters · ${length.parts} SMS · ${length.remaining} left in this part")
                when {
                    limitReached || overLimit -> Text("Limit: $MAX_SMS_PARTS SMS parts per send. Shorten the message.")
                    length.parts > 1 -> Text("Sent as ${length.parts} SMS parts. Carrier charges may apply per part.")
                }
            }
        }
    )
}
