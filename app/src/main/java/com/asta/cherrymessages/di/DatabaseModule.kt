package com.asta.cherrymessages.di

import android.content.Context
import androidx.room.Room
import com.asta.cherrymessages.data.local.AppDatabase
import com.asta.cherrymessages.data.local.dao.BlockedNumberDao
import com.asta.cherrymessages.data.local.dao.ContactDao
import com.asta.cherrymessages.data.local.dao.ConversationDao
import com.asta.cherrymessages.data.local.dao.DraftDao
import com.asta.cherrymessages.data.local.dao.MessageDao
import com.asta.cherrymessages.data.local.dao.ScheduledMessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideConversationDao(database: AppDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    fun provideContactDao(database: AppDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    fun provideDraftDao(database: AppDatabase): DraftDao {
        return database.draftDao()
    }

    @Provides
    fun provideScheduledMessageDao(database: AppDatabase): ScheduledMessageDao {
        return database.scheduledMessageDao()
    }

    @Provides
    fun provideBlockedNumberDao(database: AppDatabase): BlockedNumberDao {
        return database.blockedNumberDao()
    }
}
