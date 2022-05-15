package com.rymo.felfel.features.contacts


import android.content.Context
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
        contactDao.deleteContactAlarmByContactId(contact.id)
    }

    fun editContact(contact: Contact) {
        contactDao.updateContact(contact)
    }


    fun export() {
        excelRepository.exportContacts(contactListLiveData.value!!)
    }

    fun exportLiveData() = excelRepository.exportExcelLiveData

}
