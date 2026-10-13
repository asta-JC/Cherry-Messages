package com.asta.cherrymessages.data.repository

import com.asta.cherrymessages.data.local.dao.ScheduledMessageDao
import com.asta.cherrymessages.data.local.entity.ScheduledMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduledMessageRepository @Inject constructor(
    private val scheduledMessageDao: ScheduledMessageDao
) {
    fun getAllScheduledMessages(): Flow<List<ScheduledMessageEntity>> = scheduledMessageDao.getAllScheduledMessages()

    suspend fun scheduleMessage(threadId: Long, address: String, body: String, scheduledTimestamp: Long): Long {
        val entity = ScheduledMessageEntity(
            threadId = threadId,
            address = address,
            body = body,
            scheduledTimestamp = scheduledTimestamp,
            status = "pending"
        )
        return scheduledMessageDao.insertScheduledMessage(entity)
    }

    suspend fun cancelScheduledMessage(id: Long) {
        scheduledMessageDao.deleteScheduledMessage(id)
    }

    suspend fun updateStatus(id: Long, status: String) {
        scheduledMessageDao.updateStatus(id, status)
    }
}
