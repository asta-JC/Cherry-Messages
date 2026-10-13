package com.asta.cherrymessages.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val threadId: Long,
    val recipientAddress: String,
    val recipientName: String? = null,
    val recipientPhotoUri: String? = null,
    val snippet: String = "",
    val date: Long = 0L,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false
)
