package com.asta.cherrymessages.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object ConversationList : Screen

    @Serializable
    data class Chat(val threadId: Long, val address: String, val recipientName: String? = null) : Screen

    @Serializable
    data object NewMessage : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object StarredMessages : Screen

    @Serializable
    data object Settings : Screen
}
