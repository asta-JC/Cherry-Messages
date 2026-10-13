package com.asta.cherrymessages.data.repository

import com.asta.cherrymessages.data.local.dao.DraftDao
import com.asta.cherrymessages.data.local.entity.DraftEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftRepository @Inject constructor(
    private val draftDao: DraftDao
) {
    fun getDraft(threadId: Long): Flow<DraftEntity?> = draftDao.getDraft(threadId)

    suspend fun saveDraft(threadId: Long, body: String) {
        if (body.isBlank()) {
            draftDao.deleteDraft(threadId)
        } else {
            draftDao.upsertDraft(DraftEntity(threadId = threadId, body = body, updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun deleteDraft(threadId: Long) {
        draftDao.deleteDraft(threadId)
    }
}
