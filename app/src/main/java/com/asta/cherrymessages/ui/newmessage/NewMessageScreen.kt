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
    onBackClick: () -> Unit,
    onChatOpened: (Long, String, String?) -> Unit
) {
    val recipient by viewModel.recipient.collectAsState()
    val messageBody by viewModel.messageBody.collectAsState()

    Scaffold(
        containerColor = CherryBackground,
        topBar = {
            TopAppBar(
                title = { Text("New message", style = MaterialTheme.typography.titleMedium.copy(color = Color.White)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = recipient,
                onValueChange = { viewModel.updateRecipient(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CherryBorder, RoundedCornerShape(20.dp)),
                prefix = { Text("+", color = Color.White, fontWeight = FontWeight.Bold) },
                placeholder = { Text("998XX1234567", color = CherryTextSecondary) },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CherryPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = CherrySurface,
                    unfocusedContainerColor = CherrySurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = messageBody,
                onValueChange = { viewModel.updateMessageBody(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CherryBorder, RoundedCornerShape(20.dp)),
                placeholder = { Text("Type a message...", color = CherryTextSecondary) },
                shape = RoundedCornerShape(20.dp),
                minLines = 1,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CherryPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = CherrySurface,
                    unfocusedContainerColor = CherrySurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Button(
                onClick = {
                    viewModel.sendNewMessage { threadId, address ->
                        onChatOpened(threadId, address, null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CherryPrimary)
            ) {
                Icon(Icons.Rounded.Send, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SEND MESSAGE", style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontSize = 14.sp))
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
