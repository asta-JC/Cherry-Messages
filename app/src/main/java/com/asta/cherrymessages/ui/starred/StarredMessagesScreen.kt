package com.asta.cherrymessages.ui.starred

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asta.cherrymessages.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarredMessagesScreen(
    viewModel: StarredMessagesViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onMessageClick: (Long, String) -> Unit
) {
    val starredMessages by viewModel.starredMessages.collectAsState()

    Scaffold(
        containerColor = CherryBackground,
        topBar = {
            TopAppBar(
                title = { Text("Starred messages", style = MaterialTheme.typography.titleMedium.copy(color = Color.White)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CherrySurface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CherryBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(starredMessages, key = { it.id }) { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CherryBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CherrySurface),
                    onClick = { onMessageClick(message.threadId, message.address) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = message.address,
                                style = MaterialTheme.typography.labelMedium.copy(color = CherryPrimary)
                            )
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = CherryPrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = message.body,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StarredPreview() {
    CherryMessagesTheme {
        // StarredMessagesScreen(...)
    }
}
