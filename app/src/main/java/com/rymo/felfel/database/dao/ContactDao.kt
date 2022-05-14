package com.rymo.felfel.database.dao

import androidx.room.*
import com.rymo.felfel.model.AlarmContact
import com.rymo.felfel.model.Contact

@Dao
interface ContactDao {

    @Insert
    fun insetContact(contact: Contact): Long

    @Insert
    fun insetContactAlarm(contact: AlarmContact): Long

    @Update
    fun updateContactAlarm(contact: AlarmContact)

    @Update
    fun updateContact(contact: Contact)

    @Query("DELETE FROM tbl_alarm_contact WHERE alarmId = :alarmId")
    fun deleteContactAlarm(alarmId: Long)

    @Query("DELETE FROM tbl_alarm_contact WHERE contactId = :contactId")
    fun deleteContactAlarmByContactId(contactId: Long)

    @Delete
    fun deleteContact(contact: Contact)

    @Query("SELECT * FROM tbl_alarm_contact WHERE alarmId=:alarmId")
    fun getContactAlarm(alarmId: Long): List<AlarmContact>

    @Query("SELECT * FROM tbl_contact")
    fun getContact(): List<Contact>

    @Query("SELECT * FROM tbl_contact WHERE id = :contactId")
    fun getContact(contactId: Long): Contact?

}
