package com.asta.cherrymessages.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "messages_fts")
@Fts4(contentEntity = MessageEntity::class)
data class MessageFtsEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowid: Long,
    val body: String,
    val address: String
)
