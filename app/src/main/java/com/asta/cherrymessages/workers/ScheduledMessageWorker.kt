package com.asta.cherrymessages.workers

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.asta.cherrymessages.data.local.dao.MessageDao
import com.asta.cherrymessages.data.local.dao.ScheduledMessageDao
import com.asta.cherrymessages.data.local.entity.MessageEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class ScheduledMessageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val scheduledMessageDao: ScheduledMessageDao,
    private val messageDao: MessageDao
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "ScheduledMessageWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val scheduledMessages = scheduledMessageDao.getAllScheduledMessages().first()
            val currentTime = System.currentTimeMillis()
            val smsManager = applicationContext.getSystemService(SmsManager::class.java)

            for (scheduled in scheduledMessages) {
                if (scheduled.scheduledTimestamp <= currentTime && scheduled.status == "pending") {
                    try {
                        smsManager.sendTextMessage(scheduled.address, null, scheduled.body, null, null)
                        
                        val message = MessageEntity(
                            threadId = scheduled.threadId,
                            address = scheduled.address,
                            body = scheduled.body,
                            timestamp = currentTime,
                            type = "sent",
                            status = "sent",
                            isRead = true
                        )
                        messageDao.insertMessage(message)
                        scheduledMessageDao.deleteScheduledMessage(scheduled.id)
                        Log.d(TAG, "Sent scheduled message to ${scheduled.address}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to send scheduled message ${scheduled.id}", e)
                        scheduledMessageDao.updateStatus(scheduled.id, "failed")
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "ScheduledMessageWorker failed", e)
            Result.retry()
        }
    }
}
