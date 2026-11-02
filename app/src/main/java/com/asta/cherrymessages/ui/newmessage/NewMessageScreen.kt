package com.asta.cherrymessages.ui.newmessage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asta.cherrymessages.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMessageScreen(
    viewModel: NewMessageViewModel = hiltViewModel(),
    initialRecipient: String = "",
    initialBody: String = "",
    onBackClick: () -> Unit,
    onChatOpened: (Long, String, String?) -> Unit
) {
    LaunchedEffect(Unit) {
        if (initialRecipient.isNotBlank()) viewModel.updateRecipient(initialRecipient)
        if (initialBody.isNotBlank()) viewModel.updateMessageBody(initialBody)
    }
    val context = androidx.compose.ui.platform.LocalContext.current
    val contactPicker = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) result.data?.data?.let { uri ->
            context.contentResolver.query(uri, arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER, android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) {
                    val number = it.getString(it.getColumnIndexOrThrow(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val nameIndex = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val name = if (nameIndex >= 0) it.getString(nameIndex) else null
                    viewModel.selectContact(name, number)
                }
            }
        }
    }
    val recipient by viewModel.recipient.collectAsState()
    val recipientName by viewModel.recipientName.collectAsState()
    val messageBody by viewModel.messageBody.collectAsState()

    Scaffold(
        containerColor = CherryBackground,
        topBar = {
            TopAppBar(
                title = { Text("New message", style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CherrySurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CherryBackground)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (recipientName != null) {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Row(Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(recipientName.orEmpty(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(recipient, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        IconButton(onClick = { viewModel.updateRecipient(recipient) }) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit recipient", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = recipient,
                    onValueChange = viewModel::updateRecipient,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("To") },
                    placeholder = { Text("Phone number") },
                    trailingIcon = {
                        IconButton(onClick = { contactPicker.launch(android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI)) }) {
                            Icon(Icons.Rounded.Contacts, contentDescription = "Choose contact")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = CherryBorder),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
            if (recipientName != null) TextButton(onClick = { contactPicker.launch(android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI)) }) { Icon(Icons.Rounded.Contacts, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Change contact") }

            com.asta.cherrymessages.ui.components.SmsMessageField(
                value = messageBody,
                onValueChange = viewModel::updateMessageBody,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            Button(
                enabled = recipient.any { it.isDigit() } && messageBody.isNotBlank() && com.asta.cherrymessages.ui.components.smsLength(messageBody).parts <= com.asta.cherrymessages.ui.components.MAX_SMS_PARTS,
                onClick = {
                    viewModel.sendNewMessage { threadId, address ->
                        onChatOpened(threadId, address, recipientName)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CherryPrimary)
            ) {
                Icon(Icons.Rounded.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send SMS", style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp))
            }
        }
    }
}

@Preview
@Composable
fun NewMessagePreview() {
    CherryMessagesTheme {
        // NewMessageScreen(...)
    }
}
