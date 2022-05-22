package com.rymo.felfel.database.dao

import androidx.room.*
import com.rymo.felfel.model.AlarmContact
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.ContactGroup
import com.rymo.felfel.model.Group

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

    @Query("DELETE FROM tbl_contact_group WHERE contactId = :contactId AND groupId = :groupId")
    fun deleteContactFromGroup(contactId: Long,groupId : Long)

    @Query("SELECT * FROM tbl_alarm_contact WHERE alarmId=:alarmId")
    fun getContactAlarm(alarmId: Long): List<AlarmContact>

    @Query("SELECT * FROM tbl_contact")
    fun getContact(): List<Contact>

    @Query("SELECT * FROM tbl_contact WHERE id = :contactId")
    fun getContact(contactId: Long): Contact?

    @Query("SELECT * FROM tbl_group")
    fun getGroups(): List<Group>

    @Insert
    fun createGroup(group: Group): Long

    @Query("SELECT id FROM tbl_group WHERE name = :groupName")
    fun getGroup(groupName: String): Long?

    @Query("SELECT contactId FROM tbl_contact_group WHERE groupId = :groupId")
    fun getContactsGroup(groupId: Long): List<Long>

    @Query("SELECT * FROM tbl_contact WHERE id IN (:contactIds)")
    fun getContactsGroup(contactIds: List<Long>): List<Contact>

    @Insert
    fun addContactToGroup(contactGroup: ContactGroup)

    @Query("DELETE FROM tbl_contact_group WHERE contactId = :contactId AND groupId = :groupId")
    fun removeContactFromGroup(groupId: Long, contactId: Long)

    @Query("DELETE FROM tbl_contact_group WHERE contactId = :contactId")
    fun removeContactFromGroups(contactId: Long)

    @Query("DELETE FROM tbl_contact_group WHERE groupId = :groupId")
    fun removeAllContactFromGroup(groupId: Long)

    @Delete
    fun deleteGroup(group: Group)

}
