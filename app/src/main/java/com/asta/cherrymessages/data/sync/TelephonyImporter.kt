package com.asta.cherrymessages.data.sync

import android.content.Context
import android.provider.Telephony
import android.util.Log
import com.asta.cherrymessages.data.local.dao.ConversationDao
import com.asta.cherrymessages.data.local.dao.MessageDao
import com.asta.cherrymessages.data.local.entity.ConversationEntity
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.util.normalizePhoneNumber
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelephonyImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao
) {
    companion object {
        private const val TAG = "TelephonyImporter"
    }

    suspend fun importSmsIfNeeded(): Boolean = withContext(Dispatchers.IO) {
        try {
            val cursor = context.contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                arrayOf(
                    Telephony.Sms._ID,
                    Telephony.Sms.THREAD_ID,
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE,
                    Telephony.Sms.TYPE,
                    Telephony.Sms.READ
                ),
                null,
                null,
                "${Telephony.Sms.DATE} DESC"
            ) ?: return@withContext false

            val messages = mutableListOf<MessageEntity>()
            val conversationMap = mutableMapOf<Long, MutableList<MessageEntity>>()

            cursor.use {
                val idIdx = it.getColumnIndex(Telephony.Sms._ID)
                val threadIdIdx = it.getColumnIndex(Telephony.Sms.THREAD_ID)
                val addressIdx = it.getColumnIndex(Telephony.Sms.ADDRESS)
                val bodyIdx = it.getColumnIndex(Telephony.Sms.BODY)
                val dateIdx = it.getColumnIndex(Telephony.Sms.DATE)
                val typeIdx = it.getColumnIndex(Telephony.Sms.TYPE)
                val readIdx = it.getColumnIndex(Telephony.Sms.READ)

                while (it.moveToNext()) {
                    val systemId = if (idIdx >= 0) it.getLong(idIdx) else 0L
                    val threadId = if (threadIdIdx >= 0) it.getLong(threadIdIdx) else 0L
                    val rawAddress = if (addressIdx >= 0) it.getString(addressIdx) ?: "" else ""
                    val cleanAddress = normalizePhoneNumber(rawAddress)
                    val body = if (bodyIdx >= 0) it.getString(bodyIdx) ?: "" else ""
                    val date = if (dateIdx >= 0) it.getLong(dateIdx) else System.currentTimeMillis()
                    val typeInt = if (typeIdx >= 0) it.getInt(typeIdx) else Telephony.Sms.MESSAGE_TYPE_INBOX
                    val isRead = if (readIdx >= 0) it.getInt(readIdx) == 1 else true

                    val typeStr = when (typeInt) {
                        Telephony.Sms.MESSAGE_TYPE_SENT -> "sent"
                        Telephony.Sms.MESSAGE_TYPE_OUTBOX -> "outbox"
                        Telephony.Sms.MESSAGE_TYPE_FAILED -> "failed"
                        else -> "inbox"
                    }

                    val message = MessageEntity(
                        threadId = threadId,
                        address = cleanAddress,
                        body = body,
                        timestamp = date,
                        type = typeStr,
                        status = if (typeStr == "sent") "sent" else "received",
                        isRead = isRead,
                        systemSmsId = systemId
                    )
                    messages.add(message)
                    conversationMap.getOrPut(threadId) { mutableListOf() }.add(message)
                }
            }

            if (messages.isNotEmpty()) {
                messageDao.insertMessages(messages)

                val conversations = conversationMap.map { (threadId, threadMessages) ->
                    val latest = threadMessages.maxByOrNull { it.timestamp }
                    val unreadCount = threadMessages.count { !it.isRead && it.type == "inbox" }
                    ConversationEntity(
                        threadId = threadId,
                        recipientAddress = latest?.address ?: "",
                        snippet = latest?.body ?: "",
                        date = latest?.timestamp ?: System.currentTimeMillis(),
                        unreadCount = unreadCount
                    )
                }
                conversationDao.upsertConversations(conversations)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import system SMS", e)
            false
        }
    }
}
