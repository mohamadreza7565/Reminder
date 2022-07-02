package com.rymo.felfel.receiver.sms

import android.content.Intent
import com.rymo.felfel.common.*
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.database.createDataBaseInstance
import com.rymo.felfel.model.ExportSmsMessage
import com.rymo.felfel.model.InboxSmsModel
import com.rymo.felfel.model.SmsMessageModel
import com.rymo.felfel.repo.ExcelRepoImpl
import com.rymo.felfel.repo.SmsRepoImpl
import com.rymo.felfel.services.http.createApiServiceInstance
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

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
        var smsMessageList: MutableList<SmsMessageModel> = ArrayList()

        val sims = SimUtil.getSimCount()

        if (!smsMessages.isNullOrEmpty())
            smsMessageList = smsMessages.filter { it.phoneNumber == replacedPhone }.convertListToMutableList()

        contacts.find { it.phone == replacedPhone }?.let {
            smsMessageDao.saveToInbox(InboxSmsModel(0L, replacedPhone!!, message!!, it.nameAndFamily, it.companyName, Date().time))
        }

        if (!Setting.autoReplayMessage.isNullOrEmpty() && smsMessageList.isNotEmpty()) {

            if (Setting.autoReplayMessageSimId == Constants.API_ID) {
                val smsRepoImpl = SmsRepoImpl(createApiServiceInstance())
                var mobileContacts: MutableList<String> = ArrayList()
                replacedPhone?.let {
                    mobileContacts.add(replacedPhone)
                    Setting.autoReplayMessage?.let { autoReplayMessage ->
                        smsRepoImpl.sendSms(mobileContacts, autoReplayMessage)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally { }
                            .subscribe(object : SingleObserver<MutableList<Long>> {
                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onSuccess(t: MutableList<Long>) {

                                }

                                override fun onError(e: Throwable) {
                                    toast("خطا در ارتباط با سرور")
                                }

                            })
                    }
                }
            } else {
                SimUtil.sendSMS(Setting.autoReplayMessageSimId, replacedPhone, Setting.autoReplayMessage)
            }
        }

        if (smsMessageList.isNotEmpty()){
            if (Setting.workShopSimCard == Constants.API_ID) {

                val smsRepoImpl = SmsRepoImpl(createApiServiceInstance())
                var mobileContacts: MutableList<String> = ArrayList()
                contactDao.getWorkshopList().forEach {
                    mobileContacts.add(it.phone)
                }

                if (mobileContacts.isNotEmpty()) {
                    message?.let {
                        smsRepoImpl.sendSms(mobileContacts, message)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally { }
                            .subscribe(object : SingleObserver<MutableList<Long>> {
                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onSuccess(t: MutableList<Long>) {

                                }

                                override fun onError(e: Throwable) {
                                    toast("خطا در ارتباط با سرور")
                                }

                            })
                    }
                }

            }
            else {
                contactDao.getWorkshopList().forEach {
                    SimUtil.sendSMS(Setting.workShopSimCard, it.phone, message)
                }
            }
        }

        smsMessageList.forEach {

            message?.let { _repley ->
                it.replayText = _repley
                smsMessageDao.updateMessage(it)
                smsMessageDao.deleteExportMessage()
                val smsMessagesList = smsMessageDao.getAllSmsMessage(it.date, it.time)
                smsMessagesList.forEach { _smsMessage ->
                    smsMessageDao.insertExportMessage(
                        ExportSmsMessage(
                            contactName = _smsMessage.contactName,
                            phoneNumber = _smsMessage.phoneNumber,
                            date = _smsMessage.date,
                            time = _smsMessage.time,
                            textSms = _smsMessage.textSms,
                            replayText = if (_smsMessage.phoneNumber == replacedPhone) _repley else "",
                            companyName = _smsMessage.companyName
                        )
                    )
                }
            }
            export(date = it.date, time = it.time, roomAppDatabase = roomAppDatabase)
        }

    }

    private fun export(date: String, time: String, roomAppDatabase: RoomAppDatabase) {
        Timber.e("SMS_RECEIVER EXPORT -> $date - $time")
        ExcelRepoImpl(roomAppDatabase).exportReports(date = date, time = time)
    }

}
