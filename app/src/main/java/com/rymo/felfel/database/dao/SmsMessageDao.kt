package com.rymo.felfel.database.dao

import androidx.room.*
import com.rymo.felfel.model.ExportSmsMessage
import com.rymo.felfel.model.SmsMessageModel
import com.rymo.felfel.model.SmsMessageSendTime

@Dao
interface SmsMessageDao {

    @Insert
    fun insertMessage(smsMessageModel: SmsMessageModel): Long

    @Insert
    fun insertSmsMessageTime(smsMessageSendTime: SmsMessageSendTime): Long

    @Insert
    fun insertExportMessage(exportSmsMessage: ExportSmsMessage): Long

    @Query("SELECT * FROM tbl_sms_message WHERE date = :date AND time = :time AND phoneNumber = :phone")
    fun getSmsMessage(date: String, time: String, phone: String): SmsMessageModel?

    @Query("SELECT * FROM tbl_sms_message WHERE date = :date")
    fun getSmsMessage(date: String): List<SmsMessageModel>

    @Query("SELECT * FROM tbl_sms_message WHERE date = :date AND time = :time")
    fun getAllSmsMessage(date: String, time: String): List<SmsMessageModel>

    @Query("SELECT * FROM tbl_sms_message")
    fun getAllSmsMessage(): List<SmsMessageModel>

    @Query("SELECT * FROM tbl_sms_message_time")
    fun getAllSmsMessageTime(): List<SmsMessageSendTime>

    @Query("SELECT * FROM tbl_sms_message WHERE replayText != :replay AND date = :date AND time = :time")
    fun getAllReplaySmsMessage(replay: String = "", date: String, time: String): List<SmsMessageModel>

    @Query("SELECT * FROM tbl_sms_message WHERE replayText = :replay AND date = :date AND time = :time")
    fun getAllNoReplaySmsMessage(replay: String = "", date: String, time: String): List<SmsMessageModel>

    @Query("SELECT * FROM tbl_sms_message WHERE date = :date AND time = :time")
    fun getAllSmsMessageTime(date: String, time: String): List<SmsMessageModel>

    @Query("DELETE FROM tbl_sms_message WHERE date = :date AND time = :time")
    fun deleteMessage(date: String, time: String)

    @Query("DELETE FROM tbl_export_sms_message")
    fun deleteExportMessage()

    @Query("SELECT * FROM tbl_export_sms_message")
    fun getAllExportMessages(): List<ExportSmsMessage>

    @Update
    fun updateMessage(smsMessageModel: SmsMessageModel)


}
