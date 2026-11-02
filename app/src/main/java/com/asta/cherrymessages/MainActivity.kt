package com.asta.cherrymessages

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.asta.cherrymessages.ui.chat.ChatScreen
import com.asta.cherrymessages.ui.chat.ChatViewModel
import com.asta.cherrymessages.ui.conversations.ConversationListScreen
import com.asta.cherrymessages.ui.navigation.Screen
import com.asta.cherrymessages.ui.newmessage.NewMessageScreen
import com.asta.cherrymessages.ui.onboarding.OnboardingScreen
import com.asta.cherrymessages.ui.search.SearchScreen
import com.asta.cherrymessages.ui.settings.SettingsScreen
import com.asta.cherrymessages.ui.starred.StarredMessagesScreen
import com.asta.cherrymessages.ui.theme.CherryBackground
import com.asta.cherrymessages.ui.theme.CherryMessagesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CherryMessagesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CherryBackground
                ) {
                    val backStack = androidx.compose.runtime.saveable.rememberSaveable(
                        saver = androidx.compose.runtime.saveable.listSaver<androidx.compose.runtime.snapshots.SnapshotStateList<Screen>, String>(
                            save = { stack -> stack.map { kotlinx.serialization.json.Json.encodeToString(Screen.serializer(), it) } },
                            restore = { saved -> saved.map { kotlinx.serialization.json.Json.decodeFromString(Screen.serializer(), it) }.toMutableStateList() }
                        )
                    ) { mutableStateListOf<Screen>(
                        if (androidx.core.content.ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_SMS) == android.content.pm.PackageManager.PERMISSION_GRANTED && (if (android.os.Build.VERSION.SDK_INT >= 29) getSystemService(android.app.role.RoleManager::class.java).isRoleHeld(android.app.role.RoleManager.ROLE_SMS) else android.provider.Telephony.Sms.getDefaultSmsPackage(this@MainActivity) == packageName)) Screen.ConversationList else Screen.Onboarding
                    ) }

                    LaunchedEffect(Unit) {
                        if (backStack.first() != Screen.Onboarding && (intent.hasExtra("extra_recipient") || intent.hasExtra("extra_body"))) backStack.add(Screen.NewMessage)
                    }

                    NavDisplay(
                        backStack = backStack,
                        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        )
                    ) { key ->
                        NavEntry(key) {
                            when (key) {
                                is Screen.Onboarding -> OnboardingScreen(
                                    onOnboardingFinished = {
                                        backStack.clear()
                                        backStack.add(Screen.ConversationList)
                                        if (intent.hasExtra("extra_recipient") || intent.hasExtra("extra_body")) backStack.add(Screen.NewMessage)
                                    }
                                )
                                is Screen.ConversationList -> ConversationListScreen(
                                    onChatClick = { threadId, address, name ->
                                        backStack.add(Screen.Chat(threadId, address, name))
                                    },
                                    onNewMessageClick = { backStack.add(Screen.NewMessage) },
                                    onSearchClick = { backStack.add(Screen.Search) },
                                    onStarredClick = { backStack.add(Screen.StarredMessages) },
                                    onSettingsClick = { backStack.add(Screen.Settings) }
                                )
                                is Screen.Chat -> ChatScreen(
                                    viewModel = hiltViewModel<ChatViewModel, ChatViewModel.Factory> { factory ->
                                        factory.create(key)
                                    },
                                    onBackClick = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                )
                                is Screen.NewMessage -> NewMessageScreen(
                                    initialRecipient = intent.getStringExtra("extra_recipient") ?: "",
                                    initialBody = intent.getStringExtra("extra_body") ?: "",
                                    onBackClick = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                    onChatOpened = { threadId, address, name ->
                                        backStack.removeAt(backStack.size - 1)
                                        backStack.add(Screen.Chat(threadId, address, name))
                                    }
                                )
                                is Screen.Search -> SearchScreen(
                                    onBackClick = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                    onMessageClick = { threadId, address ->
                                        backStack.add(Screen.Chat(threadId, address, null))
                                    }
                                )
                                is Screen.StarredMessages -> StarredMessagesScreen(
                                    onBackClick = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                    onMessageClick = { threadId, address ->
                                        backStack.add(Screen.Chat(threadId, address, null))
                                    }
                                )
                                is Screen.Settings -> SettingsScreen(
                                    onBackClick = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
