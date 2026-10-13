package com.asta.cherrymessages.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asta.cherrymessages.data.local.dao.BlockedNumberDao
import com.asta.cherrymessages.data.local.dao.ContactDao
import com.asta.cherrymessages.data.local.dao.ConversationDao
import com.asta.cherrymessages.data.local.dao.DraftDao
import com.asta.cherrymessages.data.local.dao.MessageDao
import com.asta.cherrymessages.data.local.dao.ScheduledMessageDao
import com.asta.cherrymessages.data.local.entity.BlockedNumberEntity
import com.asta.cherrymessages.data.local.entity.ContactCacheEntity
import com.asta.cherrymessages.data.local.entity.ConversationEntity
import com.asta.cherrymessages.data.local.entity.DraftEntity
import com.asta.cherrymessages.data.local.entity.MessageEntity
import com.asta.cherrymessages.data.local.entity.MessageFtsEntity
import com.asta.cherrymessages.data.local.entity.ScheduledMessageEntity

@Database(
    entities = [
        MessageEntity::class,
        MessageFtsEntity::class,
        ConversationEntity::class,
        ContactCacheEntity::class,
        DraftEntity::class,
        ScheduledMessageEntity::class,
        BlockedNumberEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    abstract fun contactDao(): ContactDao
    abstract fun draftDao(): DraftDao
    abstract fun scheduledMessageDao(): ScheduledMessageDao
    abstract fun blockedNumberDao(): BlockedNumberDao

    companion object {
        const val DATABASE_NAME = "cherry_messages.db"
    }
}
