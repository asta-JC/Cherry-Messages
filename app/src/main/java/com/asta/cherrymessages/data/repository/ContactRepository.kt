package com.asta.cherrymessages.data.repository

import com.asta.cherrymessages.data.local.dao.ContactDao
import com.asta.cherrymessages.data.local.entity.ContactCacheEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {
    fun getContact(lookupKey: String): Flow<ContactCacheEntity?> = contactDao.getContact(lookupKey)

    fun getContactByNumber(phoneNumber: String): Flow<ContactCacheEntity?> = contactDao.getContactByNumber(phoneNumber)

    suspend fun saveContact(contact: ContactCacheEntity) {
        contactDao.upsertContact(contact)
    }

    suspend fun saveContacts(contacts: List<ContactCacheEntity>) {
        contactDao.upsertContacts(contacts)
    }
}
