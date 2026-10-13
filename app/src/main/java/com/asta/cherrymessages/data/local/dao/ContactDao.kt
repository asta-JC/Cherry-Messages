package com.asta.cherrymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asta.cherrymessages.data.local.entity.ContactCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact_cache WHERE lookupKey = :lookupKey")
    fun getContact(lookupKey: String): Flow<ContactCacheEntity?>

    @Query("SELECT * FROM contact_cache WHERE phoneNumber = :phoneNumber OR phoneNumber LIKE '%' || :phoneNumber")
    fun getContactByNumber(phoneNumber: String): Flow<ContactCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContact(contact: ContactCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContacts(contacts: List<ContactCacheEntity>)
}
