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
import com.asta.cherrymessages.R
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CherryGlowBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CherryBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CherryBackground.copy(alpha = 0.2f))
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top-left glowing cherry circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CherryPrimary.copy(alpha = 0.30f),
                        CherryGlow.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(width * 0.10f, height * 0.12f),
                    radius = width * 0.70f
                ),
                radius = width * 0.70f,
                center = androidx.compose.ui.geometry.Offset(width * 0.10f, height * 0.12f)
            )

            // Upper-right glowing cherry circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CherryPrimaryVariant.copy(alpha = 0.25f),
                        CherryGlow.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(width * 0.90f, height * 0.28f),
                    radius = width * 0.65f
                ),
                radius = width * 0.65f,
                center = androidx.compose.ui.geometry.Offset(width * 0.90f, height * 0.28f)
            )

            // Middle-left glowing cherry translucent circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CherryPrimary.copy(alpha = 0.22f),
                        CherryGlow.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(width * 0.05f, height * 0.55f),
                    radius = width * 0.60f
                ),
                radius = width * 0.60f,
                center = androidx.compose.ui.geometry.Offset(width * 0.05f, height * 0.55f)
            )

            // Lower-right glowing cherry circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CherryPrimary.copy(alpha = 0.28f),
                        CherryGlow.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(width * 0.85f, height * 0.80f),
                    radius = width * 0.75f
                ),
                radius = width * 0.75f,
                center = androidx.compose.ui.geometry.Offset(width * 0.85f, height * 0.80f)
            )

            // Bottom-left subtle cherry orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CherryPrimaryVariant.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(width * 0.25f, height * 0.92f),
                    radius = width * 0.50f
                ),
                radius = width * 0.50f,
                center = androidx.compose.ui.geometry.Offset(width * 0.25f, height * 0.92f)
            )
        }

        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val messageInput by viewModel.messageInput.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
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
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                            )
                            Text(
                                text = "Text message",
                                style = MaterialTheme.typography.labelSmall.copy(color = CherryTextSecondary)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { viewModel.updateMessageInput(it) },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, CherryBorder, RoundedCornerShape(24.dp)),
                            placeholder = { Text("Type a message...", color = CherryTextSecondary) },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CherryPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = CherrySurfaceVariant,
                                unfocusedContainerColor = CherrySurfaceVariant,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { viewModel.sendMessage() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(CherryPrimary, CherryPrimaryVariant)
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.Transparent),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        onStarClick = { viewModel.toggleStar(message.id, message.isStarred) }
                    )
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
                        color = Color.White,
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
