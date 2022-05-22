package com.rymo.felfel.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tbl_sms_message")
data class SmsMessageModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val companyName: String,
    val date: String,
    val time: String,
    val textSms: String,
    var replayText: String = ""
)

@Entity(tableName = "tbl_sms_message_time")
data class SmsMessageSendTime(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val time: String,
    val textSms: String,
)

@Entity(tableName = "tbl_export_sms_message")
data class ExportSmsMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val companyName: String,
    val date: String,
    val time: String,
    val textSms: String,
    val replayText: String = ""
)


