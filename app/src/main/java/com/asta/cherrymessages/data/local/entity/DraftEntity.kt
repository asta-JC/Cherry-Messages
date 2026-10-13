package com.asta.cherrymessages.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey
    val threadId: Long,
    val body: String,
    val updatedAt: Long
)
