package com.asta.cherrymessages.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.RemoteInput
import com.asta.cherrymessages.data.repository.SmsRepository
import com.asta.cherrymessages.util.NotificationHelper
import com.asta.cherrymessages.util.normalizePhoneNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var smsRepository: SmsRepository

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "SmsBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                when (action) {
                    Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> {
                        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                        if (messages != null && messages.isNotEmpty()) {
                            val rawAddress = messages[0].originatingAddress ?: "Unknown"
                            val cleanAddress = normalizePhoneNumber(rawAddress)
                            val body = buildString {
                                for (msg in messages) {
                                    append(msg.messageBody ?: "")
                                }
                            }
                            val threadId = Telephony.Threads.getOrCreateThreadId(context, cleanAddress)

                            // Insert into Room DB
                            val messageId = smsRepository.insertIncomingMessage(threadId, cleanAddress, body)

                            // Insert into system content provider
                            try {
                                val values = ContentValues().apply {
                                    put(Telephony.Sms.ADDRESS, cleanAddress)
                                    put(Telephony.Sms.BODY, body)
                                    put(Telephony.Sms.DATE, System.currentTimeMillis())
                                    put(Telephony.Sms.READ, 0)
                                    put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX)
                                    put(Telephony.Sms.THREAD_ID, threadId)
                                }
                                context.contentResolver.insert(Telephony.Sms.CONTENT_URI, values)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to insert into system telephony provider", e)
                            }

                            // Show notification
                            NotificationHelper.showIncomingSmsNotification(
                                context,
                                cleanAddress,
                                body,
                                threadId,
                                messageId
                            )
                        }
                    }
                    NotificationHelper.REPLY_ACTION -> {
                        val threadId = intent.getLongExtra(NotificationHelper.EXTRA_THREAD_ID, -1L)
                        val address = intent.getStringExtra(NotificationHelper.EXTRA_ADDRESS) ?: ""
                        val cleanAddress = normalizePhoneNumber(address)
                        val remoteInput = RemoteInput.getResultsFromIntent(intent)
                        val replyText = remoteInput?.getCharSequence(NotificationHelper.KEY_TEXT_REPLY)?.toString()

                        if (threadId != -1L && cleanAddress.isNotEmpty() && !replyText.isNullOrBlank()) {
                            smsRepository.sendMessage(threadId, cleanAddress, replyText)

                            // Dismiss notification
                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.cancel(threadId.toInt())
                        }
                    }
                    NotificationHelper.MARK_READ_ACTION -> {
                        val threadId = intent.getLongExtra(NotificationHelper.EXTRA_THREAD_ID, -1L)
                        if (threadId != -1L) {
                            smsRepository.markThreadAsRead(threadId)
                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.cancel(threadId.toInt())
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling broadcast in SmsBroadcastReceiver", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
