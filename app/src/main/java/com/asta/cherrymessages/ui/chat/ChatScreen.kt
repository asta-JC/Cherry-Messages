package com.asta.cherrymessages.ui.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.repeatOnLifecycle
import com.asta.cherrymessages.R
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CherryGlowBackground(content: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(CherryBackground), content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val loadedMessages by viewModel.messages.collectAsState()
    val messages = loadedMessages ?: run {
        Box(Modifier.fillMaxSize().background(CherryBackground), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val messageInput by viewModel.messageInput.collectAsState()
    val firstUnreadId by androidx.compose.runtime.saveable.rememberSaveable(viewModel.threadId) {
        mutableStateOf(messages.firstOrNull { it.type == "inbox" && !it.isRead }?.id)
    }
    val newestFirst = remember(messages) { messages.asReversed() }
    val initialIndex = newestFirst.indexOfFirst { it.id == firstUnreadId }.coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(listState, lifecycle, messages) {
        lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.mapNotNull { it.key as? Long } }
                .collect { visibleIds ->
                    viewModel.markVisibleMessagesAsRead(messages.filter { it.id in visibleIds && !it.isRead })
                }
        }
    }
    var previousNewestId by remember { mutableStateOf(messages.lastOrNull()?.id) }
    LaunchedEffect(messages.lastOrNull()?.id) {
        val newest = messages.lastOrNull()
        if (newest != null && newest.id != previousNewestId) {
            if (listState.firstVisibleItemIndex <= 1 || newest.type == "sent" || newest.type == "outbox") listState.scrollToItem(0)
            previousNewestId = newest.id
        }
    }

    CherryGlowBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = viewModel.address,
                                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                            )
                            Text(
                                text = "Text message",
                                style = MaterialTheme.typography.labelSmall.copy(color = CherryTextSecondary)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CherrySurface.copy(alpha = 0.95f))
                )
            },
            bottomBar = {
                Surface(
                    color = CherrySurface.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.asta.cherrymessages.ui.components.SmsMessageField(
                            value = messageInput,
                            onValueChange = viewModel::updateMessageInput,
                            modifier = Modifier.weight(1f),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.sendMessage() },
                            enabled = messageInput.isNotBlank() && com.asta.cherrymessages.ui.components.smsLength(messageInput).parts <= com.asta.cherrymessages.ui.components.MAX_SMS_PARTS,
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Send")
                        }

                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.Transparent),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(newestFirst, key = { it.id }) { message ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (message.id == firstUnreadId) {
                        Text("Unread messages", color = CherryPrimary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                    }
                    MessageBubble(
                        message = message,
                        onStarClick = { viewModel.toggleStar(message.id, message.isStarred) }
                    )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: MessageEntity,
    @Suppress("UNUSED_PARAMETER") onStarClick: () -> Unit = {}
) {
    val isSent = message.type == "sent" || message.type == "outbox"
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isSent) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .border(
                    1.dp,
                    if (isSent) CherryPrimary.copy(alpha = 0.5f) else CherryBorder,
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSent) CherrySurfaceVariant.copy(alpha = 0.92f) else CherrySurface.copy(alpha = 0.92f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                if (isSent) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(CherryPrimary, CherryPrimaryVariant)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (message.isStarred) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Starred",
                            tint = CherryPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CherryTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                    if (isSent) {
                        Text(when(message.status) {
                            "failed" -> "Not sent · check SIM and SMS permission"
                            "pending" -> "Sending…"
                            "delivered" -> "Delivered"
                            else -> "Sent"
                        }, style = MaterialTheme.typography.labelSmall, color = if(message.status == "failed") CherryError else CherryTextSecondary, modifier = Modifier.weight(1f, fill = false))
                        Icon(
                            imageVector = when (message.status) {
                                "delivered" -> Icons.Rounded.DoneAll
                                "failed" -> Icons.Rounded.Error
                                "pending" -> Icons.Rounded.Schedule
                                else -> Icons.Rounded.Done
                            },
                            contentDescription = message.status,
                            tint = if (message.status == "failed") CherryError else CherryTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
