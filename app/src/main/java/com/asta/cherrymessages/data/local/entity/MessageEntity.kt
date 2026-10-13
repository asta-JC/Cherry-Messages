package com.asta.cherrymessages.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val threadId: Long,
    val address: String,
    val body: String,
    val timestamp: Long,
    val type: String, // "inbox", "sent", "outbox", "failed"
    val status: String, // "pending", "sent", "delivered", "failed"
    val isRead: Boolean = false,
    val isStarred: Boolean = false,
    val isPinned: Boolean = false,
    val systemSmsId: Long? = null
)
