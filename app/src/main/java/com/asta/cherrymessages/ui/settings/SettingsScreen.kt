package com.asta.cherrymessages.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val blockedNumbers by viewModel.blockedNumbers.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    var newBlockNumber by remember { mutableStateOf("") }

    Scaffold(
        containerColor = CherryBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CherrySurface)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CherrySurface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = CherryTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "About Cherry Messages // Asta v1.0",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CherryTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CherryBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CherryBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CherrySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Push Notifications",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Enable message notifications",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CherryTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.toggleNotifications(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CherryPrimary,
                                checkedBorderColor = CherryPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = CherrySurfaceVariant,
                                uncheckedBorderColor = CherryBorder
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CherryBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CherrySurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "Blocked Numbers & Spam",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CherryPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newBlockNumber,
                                onValueChange = { input ->
                                    newBlockNumber = input.filter { it.isDigit() }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, CherryBorder, RoundedCornerShape(14.dp)),
                                prefix = {
                                    Text(
                                        "+",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                },
                                placeholder = {
                                    Text(
                                        "998 00 123 45 67",
                                        color = CherryTextSecondary,
                                        fontSize = 12.sp
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CherryPrimary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = CherrySurfaceVariant,
                                    unfocusedContainerColor = CherrySurfaceVariant,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newBlockNumber.isNotBlank()) {
                                        viewModel.blockNumber(newBlockNumber)
                                        newBlockNumber = ""
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CherryPrimary)
                            ) {
                                Text("Block", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            items(blockedNumbers, key = { it.id }) { blocked ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CherryBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CherrySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Block, contentDescription = null, tint = CherryError, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                blocked.phoneNumber,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            )
                        }
                        IconButton(onClick = { viewModel.unblockNumber(blocked.phoneNumber) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Unblock", tint = CherryTextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CherryBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CherrySurface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = null,
                                tint = CherryPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "ABOUT CHERRY MESSAGES",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = CherryPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Cherry Messages // Asta v1.0",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            "Secure SMS Application",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CherryTextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsPreview() {
    CherryMessagesTheme {
        // SettingsScreen(...)
    }
}
