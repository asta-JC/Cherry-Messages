package com.asta.cherrymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asta.cherrymessages.data.local.entity.ScheduledMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledMessageDao {

    @Query("SELECT * FROM scheduled_messages ORDER BY scheduledTimestamp ASC")
    fun getAllScheduledMessages(): Flow<List<ScheduledMessageEntity>>

    @Query("SELECT * FROM scheduled_messages WHERE id = :id")
    suspend fun getScheduledMessage(id: Long): ScheduledMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledMessage(message: ScheduledMessageEntity): Long

    @Query("DELETE FROM scheduled_messages WHERE id = :id")
    suspend fun deleteScheduledMessage(id: Long)

    @Query("UPDATE scheduled_messages SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
