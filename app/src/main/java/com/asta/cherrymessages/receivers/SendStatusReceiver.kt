package com.asta.cherrymessages.receivers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.asta.cherrymessages.data.local.dao.MessageDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SendStatusReceiver : BroadcastReceiver() {

    @Inject
    lateinit var messageDao: MessageDao

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "SendStatusReceiver"
        const val EXTRA_MESSAGE_ID = "extra_message_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1L)
        if (messageId == -1L) return

        val sendResult = resultCode
        val pendingResult = goAsync()

        scope.launch {
            try {
                val status = synchronized(SendStatusReceiver::class.java) {
                    val prefs = context.getSharedPreferences("sms_send_results", Context.MODE_PRIVATE)
                    val key = "message_$messageId"
                    val completed = prefs.getStringSet(key, emptySet()).orEmpty().toMutableSet()
                    if (sendResult != Activity.RESULT_OK) completed.add("failed")
                    completed.add(intent.getIntExtra("part_index", 0).toString())
                    prefs.edit().putStringSet(key, completed).commit()
                    when {
                        "failed" in completed -> "failed"
                        completed.size >= intent.getIntExtra("part_count", 1) -> "sent"
                        else -> "pending"
                    }
                }
                messageDao.updateMessageStatus(messageId, status)
                messageDao.getMessageById(messageId)?.systemSmsId?.let { systemId ->
                    val values = android.content.ContentValues().apply {
                        put(android.provider.Telephony.Sms.TYPE, when(status) {
                            "sent" -> android.provider.Telephony.Sms.MESSAGE_TYPE_SENT
                            "failed" -> android.provider.Telephony.Sms.MESSAGE_TYPE_FAILED
                            else -> android.provider.Telephony.Sms.MESSAGE_TYPE_OUTBOX
                        })
                    }
                    context.contentResolver.update(android.content.ContentUris.withAppendedId(android.provider.Telephony.Sms.CONTENT_URI, systemId), values, null, null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update message status", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
