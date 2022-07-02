package com.rymo.felfel.database.dao

import android.os.WorkSource
import androidx.room.*
import androidx.room.Delete
import com.rymo.felfel.model.*

@Dao
interface ContactDao {

    @Insert
    fun insetContactWorkshop(workshopModel: WorkshopModel): Long

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
    fun deleteContactFromGroup(contactId: Long, groupId: Long)

    @Query("SELECT * FROM tbl_alarm_contact WHERE alarmId=:alarmId")
    fun getContactAlarm(alarmId: Long): List<AlarmContact>

    @Query("SELECT * FROM tbl_contact order by nameAndFamily ASC")
    fun getContact(): List<Contact>

    @Query("SELECT * FROM tbl_contact WHERE id = :contactId")
    fun getContact(contactId: Long): Contact?

    @Query("SELECT * FROM tbl_group")
    fun getGroups(): List<Group>

    @Query("SELECT * FROM tbl_workshop order by name ASC")
    fun getWorkshopList(): List<WorkshopModel>

    @Insert
    fun createGroup(group: Group): Long

    @Update
    fun editGroup(group: Group)

    @Query("SELECT id FROM tbl_group WHERE name = :groupName")
    fun getGroup(groupName: String): Long?

    @Query("SELECT contactId FROM tbl_contact_group WHERE groupId = :groupId")
    fun getContactsGroup(groupId: Long): List<Long>

    @Query("SELECT * FROM tbl_contact WHERE id IN (:contactIds) order by nameAndFamily ASC")
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

    @Delete
    fun deleteWorkshop(workshopModel: WorkshopModel)

    @Update
    fun editWorkshop(workshopModel: WorkshopModel)

}
