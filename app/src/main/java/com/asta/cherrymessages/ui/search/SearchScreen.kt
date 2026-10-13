package com.asta.cherrymessages.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onMessageClick: (Long, String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.searchResults.collectAsState()

    Scaffold(
        containerColor = CherryBackground,
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.updateQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        placeholder = { Text("Search messages...", color = CherryTextSecondary) },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = CherryTextSecondary) },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CherryPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = CherrySurfaceVariant,
                            unfocusedContainerColor = CherrySurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results, key = { it.id }) { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CherryBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CherrySurface),
                    onClick = { onMessageClick(message.threadId, message.address) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = message.address,
                            style = MaterialTheme.typography.labelMedium.copy(color = CherryPrimary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
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
fun SearchPreview() {
    CherryMessagesTheme {
        // SearchScreen(...)
    }
}
