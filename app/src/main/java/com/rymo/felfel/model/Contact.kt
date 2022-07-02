package com.rymo.felfel.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_contact")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var phone: String = " ",
    var nameAndFamily: String = " ",
    var companyName: String = " ",
    @Ignore
    val replayMessage: String = "",
    @Ignore
    var date: Long = 0L,
    @Ignore
    var selected: Boolean = false,
)

@Entity(tableName = "tbl_alarm_contact")
data class AlarmContact(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var contactId: Long,
    var alarmId: Long
)
