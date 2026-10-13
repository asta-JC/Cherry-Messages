package com.asta.cherrymessages.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.asta.cherrymessages.ui.theme.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onOnboardingFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LifecycleResumeEffect(Unit) {
        viewModel.checkDefaultSmsApp()
        onPauseOrDispose {}
    }

    val permissionsList = mutableListOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CONTACTS
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionsList.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = permissionsList)

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        viewModel.setPermissionsGranted(permissionsState.allPermissionsGranted)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CherryBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CherryPrimary, CherryPrimaryVariant)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sms,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "CHERRY MESSAGES",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 28.sp,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Secure SMS Messaging",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CherryTextSecondary
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CherryBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CherrySurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "SETUP & PERMISSIONS",
                        style = MaterialTheme.typography.titleMedium.copy(color = CherryPrimary)
                    )

                    PermissionRow(
                        title = "Permissions Access",
                        description = "Grant SMS and Contacts permissions to send and receive messages.",
                        isGranted = permissionsState.allPermissionsGranted,
                        onClick = { permissionsState.launchMultiplePermissionRequest() }
                    )

                    PermissionRow(
                        title = "Default SMS App",
                        description = "Set Cherry Messages as your default handler.",
                        isGranted = uiState.isDefaultSmsApp,
                        onClick = { viewModel.requestDefaultSmsApp(context) }
                    )

                    PermissionRow(
                        title = "Import Messages",
                        description = "Sync existing messages from device.",
                        isGranted = uiState.importComplete,
                        onClick = { viewModel.importSms() }
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (permissionsState.allPermissionsGranted) {
                            onOnboardingFinished()
                        } else {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CherryPrimary)
                ) {
                    Text(
                        text = if (permissionsState.allPermissionsGranted) "GET STARTED" else "GRANT PERMISSIONS",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cherry Messages",
                    style = MaterialTheme.typography.labelSmall.copy(color = CherryTextSecondary)
                )
            }
        }
    }
}

@Composable
fun PermissionRow(
    title: String,
    description: String,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, color = Color.White)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, color = CherryTextSecondary)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        if (isGranted) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Granted",
                tint = CherrySuccess,
                modifier = Modifier.size(28.dp)
            )
        } else {
            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CherryPrimary)
            ) {
                Text("Enable", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
