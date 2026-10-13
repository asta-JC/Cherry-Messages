package com.asta.cherrymessages.ui.conversations

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asta.cherrymessages.data.local.entity.ConversationEntity
import com.asta.cherrymessages.ui.theme.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.asta.cherrymessages.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    viewModel: ConversationListViewModel = hiltViewModel(),
    onChatClick: (Long, String, String?) -> Unit,
    onNewMessageClick: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onSearchClick: () -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onStarredClick: () -> Unit = {},
    onSettingsClick: () -> Unit
) {
    val conversations by viewModel.conversations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var conversationToDelete by remember { mutableStateOf<Long?>(null) }

    val filteredConversations = conversations.filter {
        it.recipientAddress.contains(searchQuery, ignoreCase = true) ||
                (it.recipientName?.contains(searchQuery, ignoreCase = true) == true) ||
                it.snippet.contains(searchQuery, ignoreCase = true)
    }

    if (conversationToDelete != null) {
        AlertDialog(
            onDismissRequest = { conversationToDelete = null },
            title = {
                Text("Delete Conversation", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to delete this conversation?",
                    color = CherryTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        conversationToDelete?.let { threadId ->
                            viewModel.deleteConversation(threadId)
                        }
                        conversationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CherryError)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { conversationToDelete = null }
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = CherrySurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        containerColor = CherryBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CherryPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MarkChatUnread,
                                contentDescription = "Cherry Messages icon",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            "CHERRY MESSAGES",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontSize = 14.sp
                            ),
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CherrySurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewMessageClick,
                containerColor = CherryPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = "New Message")
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CherrySurface)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cherry Messages",
                    style = MaterialTheme.typography.labelSmall.copy(color = CherryTextSecondary)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CherryBackground)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, CherryBorder, RoundedCornerShape(20.dp)),
                placeholder = { Text("Search messages...", color = CherryTextSecondary) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = CherryTextSecondary) },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CherryPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = CherrySurface,
                    unfocusedContainerColor = CherrySurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredConversations, key = { it.threadId }) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = { onChatClick(conversation.threadId, conversation.recipientAddress, conversation.recipientName) },
                        onPinClick = { viewModel.togglePin(conversation.threadId, conversation.isPinned) },
                        onDeleteClick = { conversationToDelete = conversation.threadId }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConversationItem(
    conversation: ConversationEntity,
    onClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateStr = remember(conversation.date) { dateFormat.format(Date(conversation.date)) }

    val displayName = conversation.recipientName?.ifBlank { null } ?: conversation.recipientAddress

    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleteClick()
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CherryError.copy(alpha = 0.85f))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (conversation.isPinned) CherryPrimary else CherryBorder,
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CherrySurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onPinClick
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_pic),
                    contentDescription = displayName,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.labelSmall.copy(color = CherryTextSecondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = conversation.snippet.ifBlank { "No messages yet" },
                            style = MaterialTheme.typography.bodyMedium.copy(color = CherryTextSecondary),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (conversation.unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(CherryPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = conversation.unreadCount.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 11.sp)
                                )
                            }
                        }

                        if (conversation.isPinned) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Rounded.PushPin,
                                contentDescription = "Pinned",
                                tint = CherryPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ConversationListPreview() {
    CherryMessagesTheme {
        // ConversationListScreen(...)
    }
}
