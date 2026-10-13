package com.asta.cherrymessages

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
                    val backStack = remember { mutableStateListOf<Screen>(Screen.ConversationList) }

                    NavDisplay(
                        backStack = backStack,
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
