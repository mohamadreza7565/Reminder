package com.rymo.felfel.features.reports

import androidx.lifecycle.MutableLiveData
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.BaseExceptionExportExcel
import com.rymo.felfel.common.convertListToMutableList
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.model.SmsMessageModel
import com.rymo.felfel.model.SmsMessageSendTime
import com.rymo.felfel.repo.ExcelRepoImpl
import timber.log.Timber

class ReportsViewModel(
    private val roomAppDatabase: RoomAppDatabase,
    private val excelRepository: ExcelRepoImpl
) : Base.BaseViewModel() {

    val exportExcelLiveData = MutableLiveData<BaseExceptionExportExcel>()
    val smsMessageDao = roomAppDatabase.smsMessageDao()
    val allSmsMessagesTimeLiveData = MutableLiveData<MutableList<SmsMessageSendTime>>()
    val noReplaySmsMessagesLiveData = MutableLiveData<MutableList<SmsMessageModel>>()
    val doReplaySmsMessagesLiveData = MutableLiveData<MutableList<SmsMessageModel>>()
    val allSmsMessagesLiveData = MutableLiveData<MutableList<SmsMessageModel>>()

    init {
        getAllSendMessagesTime()
    }

    private fun getAllSendMessagesTime() {
        allSmsMessagesTimeLiveData.postValue(smsMessageDao.getAllSmsMessageTime().convertListToMutableList())
    }

    fun getNoReplaySendMessages(date: String, time: String) {
        noReplaySmsMessagesLiveData.postValue(smsMessageDao.getAllNoReplaySmsMessage(date = date, time = time).convertListToMutableList())
    }

    fun getDoReplaySendMessages(date: String, time: String) {
        doReplaySmsMessagesLiveData.postValue(smsMessageDao.getAllReplaySmsMessage(date = date, time = time).convertListToMutableList())
    }

    fun getAllSendMessages(date: String, time: String) {
        allSmsMessagesLiveData.postValue(smsMessageDao.getAllSmsMessage(date = date, time = time).convertListToMutableList())
    }

}
