package com.asta.cherrymessages.receivers

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
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
class SmsDeliverReceiver : BroadcastReceiver() {

    @Inject
    lateinit var smsRepository: SmsRepository

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "SmsDeliverReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val pendingResult = goAsync()
            receiverScope.launch {
                try {
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
                        val messageId = smsRepository.insertIncomingMessage(threadId, cleanAddress, body)

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

                        NotificationHelper.showIncomingSmsNotification(
                            context,
                            cleanAddress,
                            body,
                            threadId,
                            messageId
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling SMS_DELIVER", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
