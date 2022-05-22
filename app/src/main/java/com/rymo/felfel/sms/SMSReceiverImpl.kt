package com.rymo.felfel.receiver.sms

import android.content.Intent
import com.rymo.felfel.common.PersianCalendar
import com.rymo.felfel.common.convertArabic
import com.rymo.felfel.common.toast
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.database.createDataBaseInstance
import com.rymo.felfel.model.ExportSmsMessage
import com.rymo.felfel.model.SmsMessageModel
import com.rymo.felfel.repo.ExcelRepoImpl
import timber.log.Timber

class SMSReceiverImpl : SMSReceiver() {

    override fun onMessageReceived(intent: Intent?, phone: String?, message: String?) {

        Timber.e("SMS_RECEIVER -> onMessageReceived")

        val pCal = PersianCalendar.getInstance()
        val roomAppDatabase: RoomAppDatabase = createDataBaseInstance(context!!)
        val smsMessageDao = roomAppDatabase.smsMessageDao()
        val contactDao = roomAppDatabase.contactDao()
        val contacts = contactDao.getContact()
        val replacedPhone = phone?.replace("+98", "0")
        Timber.e("SMS_RECEIVER contact list -> $phone - $message")
        Timber.e("SMS_RECEIVER contact list -> $contacts")

        val time = pCal.time.convertArabic()
        val date = pCal.iranianDate.convertArabic()
        val smsMessages = smsMessageDao.getSmsMessage(date)
        val allSms = smsMessageDao.getAllSmsMessage()

        Timber.e("SMS_RECEIVER list sms -> $allSms - $date - $replacedPhone")
        var smsMessage: SmsMessageModel? = null

        if (!smsMessages.isNullOrEmpty())
            smsMessage = smsMessages.last()


        contacts.forEach {
            Timber.e("SMS_RECEIVER contact item -> $it")
            if (it.phone == replacedPhone) {
                Timber.e("SMS_RECEIVER -> ${it.phone} == $replacedPhone")
                Timber.e("SMS_RECEIVER smsMessage -> $smsMessage")
                if (smsMessage != null) {
                    Timber.e("SMS_RECEIVER update")
                    message?.let { _repley ->
                        smsMessage.replayText = _repley
                        smsMessageDao.updateMessage(smsMessage)
                        smsMessageDao.deleteExportMessage()
                        val smsMessagesList = smsMessageDao.getAllSmsMessage(smsMessage.date, smsMessage.time)
                        smsMessagesList.forEach { _smsMessage ->
                            smsMessageDao.insertExportMessage(
                                ExportSmsMessage(
                                    contactName = _smsMessage.contactName,
                                    phoneNumber = _smsMessage.phoneNumber,
                                    date = _smsMessage.date,
                                    time = _smsMessage.time,
                                    textSms = _smsMessage.textSms,
                                    replayText = _repley,
                                    companyName = _smsMessage.companyName
                                )
                            )
                        }
                    }
                    export(date = smsMessage.date, time = smsMessage.time)
                }
            }
        }
    }

    private fun export(date: String, time: String) {
        Timber.e("SMS_RECEIVER EXPORT -> $date - $time")
        ExcelRepoImpl().exportReports(date = date, time = time)
    }

}
