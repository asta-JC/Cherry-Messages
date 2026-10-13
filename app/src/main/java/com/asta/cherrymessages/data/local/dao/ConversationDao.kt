package com.asta.cherrymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asta.cherrymessages.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, date DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations")
    suspend fun getAllConversationsList(): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE threadId = :threadId")
    fun getConversation(threadId: Long): Flow<ConversationEntity?>

    @Query("SELECT * FROM conversations WHERE threadId = :threadId")
    suspend fun getConversationByThreadId(threadId: Long): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE recipientAddress = :address OR recipientAddress = :altAddress")
    suspend fun getConversationByAddress(address: String, altAddress: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversations(conversations: List<ConversationEntity>)

    @Query("DELETE FROM conversations WHERE threadId = :threadId")
    suspend fun deleteConversation(threadId: Long)

    @Query("UPDATE conversations SET isPinned = :isPinned WHERE threadId = :threadId")
    suspend fun updatePinStatus(threadId: Long, isPinned: Boolean)

    @Query("UPDATE conversations SET isArchived = :isArchived WHERE threadId = :threadId")
    suspend fun updateArchiveStatus(threadId: Long, isArchived: Boolean)

    @Query("UPDATE conversations SET isMuted = :isMuted WHERE threadId = :threadId")
    suspend fun updateMuteStatus(threadId: Long, isMuted: Boolean)
}
