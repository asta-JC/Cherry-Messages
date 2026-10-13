package com.asta.cherrymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asta.cherrymessages.data.local.entity.BlockedNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedNumberDao {

    @Query("SELECT * FROM blocked_numbers ORDER BY blockedAt DESC")
    fun getBlockedNumbers(): Flow<List<BlockedNumberEntity>>

    @Query("SELECT COUNT(*) > 0 FROM blocked_numbers WHERE phoneNumber = :phoneNumber")
    suspend fun isBlocked(phoneNumber: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedNumber(blockedNumber: BlockedNumberEntity)

    @Query("DELETE FROM blocked_numbers WHERE phoneNumber = :phoneNumber")
    suspend fun deleteBlockedNumber(phoneNumber: String)
}
