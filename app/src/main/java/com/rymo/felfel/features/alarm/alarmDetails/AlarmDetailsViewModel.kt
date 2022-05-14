package com.rymo.felfel.features.alarm.alarmDetails

import com.rymo.felfel.common.Base
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.database.dao.ContactDao
import com.rymo.felfel.model.AlarmContact
import com.rymo.felfel.model.Contact

class AlarmDetailsViewModel(private val appDatabase: RoomAppDatabase) : Base.BaseViewModel() {

    private var contactDao: ContactDao = appDatabase.contactDao()
    var contacts: MutableList<Contact> = ArrayList()

    fun getContacts(alarmId: Long = -1) {
        val contactList = contactDao.getContact()
        if (!contactList.isNullOrEmpty()) {
            contacts.clear()
            contacts.addAll(contactList)
            if (alarmId != -1L) {
                val contactsAlarm = contactDao.getContactAlarm(alarmId)
                contactsAlarm.forEach { _contactAlarm ->
                    contacts.forEachIndexed { _index, _contact ->
                        if (_contact.id == _contactAlarm.contactId) {
                            contacts[_index].selected = true
                        }
                    }
                }
            }
        }
    }

    fun addAlarmContacts(alarmId: Long) {
        contacts.forEach {
            if (it.selected) {
                val alarmContact = AlarmContact(0, it.id, alarmId)
                contactDao.insetContactAlarm(alarmContact)
            }
        }
    }

    fun updateContactAlarm(alarmId: Long){
        contactDao.deleteContactAlarm(alarmId)
        addAlarmContacts(alarmId)
    }

}
