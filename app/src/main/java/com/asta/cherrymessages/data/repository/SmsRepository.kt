package com.asta.cherrymessages.data.repository

import android.app.Application
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.asta.cherrymessages.data.local.dao.ConversationDao
import com.asta.cherrymessages.data.local.dao.MessageDao
import com.asta.cherrymessages.data.local.entity.ConversationEntity
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.data.sync.TelephonyImporter
import com.asta.cherrymessages.receivers.DeliveryStatusReceiver
import com.asta.cherrymessages.receivers.SendStatusReceiver
import com.asta.cherrymessages.util.normalizePhoneNumber
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepository @Inject constructor(
    private val application: Application,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val telephonyImporter: TelephonyImporter
) {
    fun getAllConversations(): Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    fun getConversation(threadId: Long): Flow<ConversationEntity?> = conversationDao.getConversation(threadId)

    fun getMessagesForThread(threadId: Long): Flow<List<MessageEntity>> = messageDao.getMessagesForThread(threadId)

    fun searchMessages(query: String): Flow<List<MessageEntity>> = messageDao.searchMessages(query)

    suspend fun getOrCreateThreadId(address: String): Long {
        val cleanAddress = normalizePhoneNumber(address)
        if (cleanAddress.isBlank()) return 0L

        // 1. Try system Telephony provider thread ID using cleanAddress
        val systemThreadId = try {
            Telephony.Threads.getOrCreateThreadId(application, cleanAddress)
        } catch (_: Exception) {
            0L
        }

        if (systemThreadId > 0L) {
            val existingByThread = conversationDao.getConversationByThreadId(systemThreadId)
            if (existingByThread != null) {
                return existingByThread.threadId
            }
        }

        // 2. Check if a conversation exists in Room by normalized address
        val rawWithoutPlus = if (cleanAddress.startsWith("+")) cleanAddress.substring(1) else cleanAddress
        val existingByAddress = conversationDao.getConversationByAddress(cleanAddress, rawWithoutPlus)
        if (existingByAddress != null) {
            return existingByAddress.threadId
        }

        val allConversations = conversationDao.getAllConversationsList()
        val matched = allConversations.find {
            normalizePhoneNumber(it.recipientAddress) == cleanAddress
        }
        if (matched != null) {
            return matched.threadId
        }

        return if (systemThreadId > 0L) systemThreadId else cleanAddress.hashCode().toLong()
    }

    suspend fun sendMessage(threadId: Long, address: String, body: String): Long {
        val cleanAddress = normalizePhoneNumber(address)
        val resolvedThreadId = if (threadId <= 0L) getOrCreateThreadId(cleanAddress) else threadId

        val timestamp = System.currentTimeMillis()
        val message = MessageEntity(
            threadId = resolvedThreadId,
            address = cleanAddress,
            body = body,
            timestamp = timestamp,
            type = "sent",
            status = "pending",
            isRead = true
        )
        val id = messageDao.insertMessage(message)

        // Safe system telephony provider insert (Sent box)
        try {
            val values = ContentValues().apply {
                put(Telephony.Sms.ADDRESS, cleanAddress)
                put(Telephony.Sms.BODY, body)
                put(Telephony.Sms.DATE, timestamp)
                put(Telephony.Sms.READ, 1)
                put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_SENT)
                put(Telephony.Sms.THREAD_ID, resolvedThreadId)
            }
            application.contentResolver.insert(Telephony.Sms.CONTENT_URI, values)
        } catch (e: Exception) {
            Log.w("SmsRepository", "Could not insert sent SMS into system provider", e)
        }

        // Update or insert conversation
        val conversation = ConversationEntity(
            threadId = resolvedThreadId,
            recipientAddress = cleanAddress,
            snippet = body,
            date = timestamp,
            unreadCount = 0
        )
        conversationDao.upsertConversation(conversation)

        // Check SEND_SMS permission
        val hasPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Log.w("SmsRepository", "SEND_SMS permission not granted, marking as sent locally")
            messageDao.updateMessageStatus(id, "sent")
            return id
        }

        // Send via SmsManager with multipart support and FLAG_IMMUTABLE pending intents
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                application.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(body)
            val sentIntents = ArrayList<PendingIntent>()
            val deliveryIntents = ArrayList<PendingIntent>()

            for (i in parts.indices) {
                val sentIntent = Intent(application, SendStatusReceiver::class.java).apply {
                    putExtra(SendStatusReceiver.EXTRA_MESSAGE_ID, id)
                }
                sentIntents.add(
                    PendingIntent.getBroadcast(
                        application,
                        (id * 100 + i).toInt(),
                        sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )

                val deliveryIntent = Intent(application, DeliveryStatusReceiver::class.java).apply {
                    putExtra(DeliveryStatusReceiver.EXTRA_MESSAGE_ID, id)
                }
                deliveryIntents.add(
                    PendingIntent.getBroadcast(
                        application,
                        (id * 100 + i + 10000).toInt(),
                        deliveryIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(cleanAddress, null, parts, sentIntents, deliveryIntents)
            } else {
                smsManager.sendTextMessage(cleanAddress, null, body, sentIntents.firstOrNull(), deliveryIntents.firstOrNull())
            }
        } catch (e: Exception) {
            Log.e("SmsRepository", "Failed to send SMS via SmsManager", e)
            messageDao.updateMessageStatus(id, "failed")
        }

        return id
    }

    suspend fun insertIncomingMessage(threadId: Long, address: String, body: String): Long {
        val cleanAddress = normalizePhoneNumber(address)
        val resolvedThreadId = if (threadId <= 0L) getOrCreateThreadId(cleanAddress) else threadId

        val message = MessageEntity(
            threadId = resolvedThreadId,
            address = cleanAddress,
            body = body,
            timestamp = System.currentTimeMillis(),
            type = "inbox",
            status = "received",
            isRead = false
        )
        val id = messageDao.insertMessage(message)
        
        val conversation = ConversationEntity(
            threadId = resolvedThreadId,
            recipientAddress = cleanAddress,
            snippet = body,
            date = message.timestamp,
            unreadCount = 1
        )
        conversationDao.upsertConversation(conversation)
        return id
    }

    suspend fun syncSystemSms(): Boolean {
        return telephonyImporter.importSmsIfNeeded()
    }

    suspend fun markThreadAsRead(threadId: Long) {
        messageDao.markThreadAsRead(threadId, true)
    }

    suspend fun deleteThread(threadId: Long) {
        messageDao.deleteMessagesForThread(threadId)
        conversationDao.deleteConversation(threadId)
    }

    suspend fun updatePinStatus(threadId: Long, isPinned: Boolean) {
        conversationDao.updatePinStatus(threadId, isPinned)
    }

    suspend fun updateArchiveStatus(threadId: Long, isArchived: Boolean) {
        conversationDao.updateArchiveStatus(threadId, isArchived)
    }

    suspend fun updateMuteStatus(threadId: Long, isMuted: Boolean) {
        conversationDao.updateMuteStatus(threadId, isMuted)
    }

    fun getStarredMessages(): Flow<List<MessageEntity>> = messageDao.getStarredMessages()

    suspend fun updateStarStatus(messageId: Long, isStarred: Boolean) {
        messageDao.updateStarStatus(messageId, isStarred)
    }
}
