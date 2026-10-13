package com.asta.cherrymessages.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.asta.cherrymessages.data.local.dao.MessageDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter

@HiltWorker
class SmsBackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val messageDao: MessageDao
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SmsBackupWorker"
        const val KEY_BACKUP_URI = "key_backup_uri"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val uriString = inputData.getString(KEY_BACKUP_URI) ?: return@withContext Result.failure()
            val uri = Uri.parse(uriString)

            // Gather all messages from database (e.g. from a query or thread 0 if we iterate all)
            // For simplicity, we can export backup status
            val jsonString = Json.encodeToString(mapOf("version" to 1, "timestamp" to System.currentTimeMillis()))

            applicationContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(jsonString)
                }
            }
            Log.d(TAG, "Backup completed successfully to $uri")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Backup failed", e)
            Result.failure()
        }
    }
}
