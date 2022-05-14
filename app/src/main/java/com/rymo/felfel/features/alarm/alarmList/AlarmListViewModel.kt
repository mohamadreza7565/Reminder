package com.rymo.felfel.features.alarm.alarmList

import com.rymo.felfel.common.Base
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.database.dao.ContactDao

class AlarmListViewModel(private val appDatabase: RoomAppDatabase) : Base.BaseViewModel() {

    private var contactDao: ContactDao = appDatabase.contactDao()

    fun deleteContactsAlarm(alarmId : Int){
        contactDao.deleteContactAlarm(alarmId.toLong())
    }

}
