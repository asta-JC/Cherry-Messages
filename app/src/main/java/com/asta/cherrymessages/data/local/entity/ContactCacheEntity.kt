package com.asta.cherrymessages.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "contact_cache")
data class ContactCacheEntity(
    @PrimaryKey
    val lookupKey: String,
    val displayName: String,
    val phoneNumber: String,
    val photoUri: String? = null,
    val email: String? = null
)
