package com.rymo.felfel.features.contacts

import androidx.lifecycle.MutableLiveData
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.convertListToMutableList
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.database.dao.ContactDao
import com.rymo.felfel.model.Contact

class ContactsViewModel(private val appDatabase: RoomAppDatabase) : Base.BaseViewModel() {

    private var contactDao: ContactDao = appDatabase.contactDao()
    public val contactListLiveData = MutableLiveData<MutableList<Contact>>()

    init {
        getContacts()
    }

    fun getContacts() {
        contactListLiveData.postValue(contactDao.getContact().convertListToMutableList())
    }


    fun addContact(contact: Contact) {
        contactDao.insetContact(contact)
    }

    fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    fun editContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

}
