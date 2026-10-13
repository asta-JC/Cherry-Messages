package com.asta.cherrymessages.data.repository

import com.asta.cherrymessages.data.local.dao.BlockedNumberDao
import com.asta.cherrymessages.data.local.entity.BlockedNumberEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedNumberRepository @Inject constructor(
    private val blockedNumberDao: BlockedNumberDao
) {
    fun getBlockedNumbers(): Flow<List<BlockedNumberEntity>> = blockedNumberDao.getBlockedNumbers()

    suspend fun isBlocked(phoneNumber: String): Boolean = blockedNumberDao.isBlocked(phoneNumber)

    suspend fun blockNumber(phoneNumber: String) {
        val entity = BlockedNumberEntity(
            phoneNumber = phoneNumber,
            blockedAt = System.currentTimeMillis()
        )
        blockedNumberDao.insertBlockedNumber(entity)
    }

    suspend fun unblockNumber(phoneNumber: String) {
        blockedNumberDao.deleteBlockedNumber(phoneNumber)
    }
}
