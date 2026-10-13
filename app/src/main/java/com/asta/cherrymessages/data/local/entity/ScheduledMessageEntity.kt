package com.asta.cherrymessages.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "scheduled_messages")
data class ScheduledMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val threadId: Long,
    val address: String,
    val body: String,
    val scheduledTimestamp: Long,
    val status: String // "pending", "sent", "failed"
)
