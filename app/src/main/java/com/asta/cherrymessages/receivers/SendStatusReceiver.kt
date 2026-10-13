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

        val pendingResult = goAsync()

        scope.launch {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    messageDao.updateMessageStatus(messageId, "sent")
                    Log.d(TAG, "Message $messageId status updated to sent (resultCode=$resultCode)")
                } else {
                    messageDao.updateMessageStatus(messageId, "failed")
                    Log.w(TAG, "Message $messageId status updated to failed (resultCode=$resultCode)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update message status", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
