package com.asta.cherrymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asta.cherrymessages.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun getMessagesForThread(threadId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Long): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE systemSmsId = :systemId LIMIT 1")
    suspend fun getBySystemId(systemId: Long): MessageEntity?

    @Query("UPDATE messages SET systemSmsId = :systemId WHERE id = :id")
    suspend fun linkSystemId(id: Long, systemId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: Long, status: String)

    @Query("UPDATE messages SET isRead = :isRead WHERE threadId = :threadId")
    suspend fun markThreadAsRead(threadId: Long, isRead: Boolean = true)

    @Query("UPDATE messages SET isRead = 1 WHERE threadId = :threadId AND id IN (:ids)")
    suspend fun markMessagesAsRead(threadId: Long, ids: List<Long>)

    @Query("SELECT COUNT(*) FROM messages WHERE threadId = :threadId AND type = 'inbox' AND isRead = 0")
    suspend fun unreadCount(threadId: Long): Int

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE threadId = :threadId")
    suspend fun deleteMessagesForThread(threadId: Long)

    @Query("""
        SELECT m.* FROM messages m
        JOIN messages_fts f ON m.id = f.rowid
        WHERE messages_fts MATCH :query
        ORDER BY m.timestamp DESC
    """)
    fun searchMessages(query: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE isStarred = 1 ORDER BY timestamp DESC")
    fun getStarredMessages(): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET isStarred = :isStarred WHERE id = :messageId")
    suspend fun updateStarStatus(messageId: Long, isStarred: Boolean)
}
