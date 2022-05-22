package com.rymo.felfel.features.contacts


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ajts.androidmads.library.SQLiteToExcel
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.BaseExceptionExportExcel
import com.rymo.felfel.common.convertListToMutableList
import com.rymo.felfel.common.toast
import com.rymo.felfel.configuration.AlarmApplication
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.database.dao.ContactDao
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.ContactGroup
import com.rymo.felfel.model.Group
import com.rymo.felfel.repo.ExcelRepoImpl
import com.rymo.felfel.repo.ExcelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File


class ContactsViewModel(
    private val appDatabase: RoomAppDatabase,
    private val excelRepository: ExcelRepoImpl
) : Base.BaseViewModel() {

    private var contactDao: ContactDao = appDatabase.contactDao()
    public val contactListLiveData = MutableLiveData<MutableList<Contact>>()
    public val groupListLiveData = MutableLiveData<MutableList<Group>>()
    public val contactsGroupListLiveData = MutableLiveData<MutableList<Contact>>()
    public val contacts: MutableList<Contact> = ArrayList()
    public val groups: MutableList<Group> = ArrayList()
    public val contactsGroup: MutableList<ContactGroup> = ArrayList()

    init {
        getContacts()
        getGroups()
    }

    fun getContacts() {
        contactListLiveData.postValue(contactDao.getContact().convertListToMutableList())
    }

    fun getGroups() = groupListLiveData.postValue(contactDao.getGroups().convertListToMutableList())

    fun getContactsGroup(groupId: Long) : MutableList<Contact> {
        val contactsGroup = contactDao.getContactsGroup(groupId).convertListToMutableList()
        val contacts = contactDao.getContactsGroup(contactsGroup)
        contactsGroupListLiveData.postValue(contacts.convertListToMutableList())
        return contacts.convertListToMutableList()
    }

    fun getContactsGroupList(groupId: Long) = contactDao.getContactsGroup(groupId)

    fun addContact(contact: Contact) {
        contactDao.insetContact(contact)
    }

    fun addGroup(group: Group) {
        contactDao.createGroup(group)
    }

    fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
        contactDao.deleteContactAlarmByContactId(contact.id)
        contactDao.removeContactFromGroups(contact.id)
    }

    fun deleteContactFromGroup(contactId: Long, groupId: Long) {
        contactDao.deleteContactFromGroup(contactId, groupId)
    }

    fun deleteGroup(group: Group) {
        contactDao.deleteGroup(group)
        contactDao.removeAllContactFromGroup(group.id)
    }

    fun removeAllContactFromGroup(groupId: Long) {
        contactDao.removeAllContactFromGroup(groupId)
    }

    fun addContactsToGroup(groupId: Long) {
        contactListLiveData.value?.let {
            it.forEach { _contact ->
                if (_contact.selected)
                    contactDao.addContactToGroup(ContactGroup(0, _contact.id, groupId))
            }
        }
    }

    fun editContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    fun contactsGroup(group: Group) = contactDao.getContactsGroup(group.id).convertListToMutableList()


    fun export() {
        excelRepository.exportContacts(contactListLiveData.value!!)
    }

    fun exportLiveData() = excelRepository.exportExcelLiveData

}
