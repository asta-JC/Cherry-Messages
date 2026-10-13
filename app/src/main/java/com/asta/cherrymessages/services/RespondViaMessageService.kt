package com.asta.cherrymessages.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import com.asta.cherrymessages.data.repository.SmsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RespondViaMessageService : Service() {

    @Inject
    lateinit var smsRepository: SmsRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "RespondViaMessageService"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action == "android.intent.action.RESPOND_VIA_MESSAGE") {
            val uri: Uri? = intent.data
            val recipient = uri?.schemeSpecificPart ?: intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            val messageText = intent.getStringExtra(Intent.EXTRA_TEXT)

            if (!recipient.isNullOrBlank() && !messageText.isNullOrBlank()) {
                serviceScope.launch {
                    try {
                        val threadId = Telephony.Threads.getOrCreateThreadId(applicationContext, recipient)
                        val smsManager = getSystemService(SmsManager::class.java)
                        smsManager.sendTextMessage(recipient, null, messageText, null, null)
                        smsRepository.sendMessage(threadId, recipient, messageText)
                        Log.d(TAG, "Successfully sent quick response to $recipient")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to send quick response", e)
                    } finally {
                        stopSelf(startId)
                    }
                }
            } else {
                stopSelf(startId)
            }
        } else {
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }
}
