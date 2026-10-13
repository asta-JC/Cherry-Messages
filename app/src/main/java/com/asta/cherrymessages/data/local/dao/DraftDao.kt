package com.asta.cherrymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asta.cherrymessages.data.local.entity.DraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {

    @Query("SELECT * FROM drafts WHERE threadId = :threadId")
    fun getDraft(threadId: Long): Flow<DraftEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDraft(draft: DraftEntity)

    @Query("DELETE FROM drafts WHERE threadId = :threadId")
    suspend fun deleteDraft(threadId: Long)
}
