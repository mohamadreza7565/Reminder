package com.rymo.felfel.features.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.rymo.felfel.R
import com.rymo.felfel.common.*
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.databinding.ActivityMainBinding
import com.rymo.felfel.features.alarm.alarmList.AlarmsListActivity
import com.rymo.felfel.features.contacts.ContactsActivity
import com.rymo.felfel.receiver.sms.SMSReceiverImpl


class MainActivity : Base.BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        fun start(mActivity: Activity) {
            Intent(mActivity, MainActivity::class.java).also {
                startActivity(mActivity, it)
            }
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()


    }


    private fun init() {
        initSmsReceivedService()
        initClick()
    }

    private fun initClick() {

        binding.llContact.setOnClickListener { startActivity(Intent(this, ContactsActivity::class.java)) }

        binding.llAlarm.setOnClickListener { startActivity(Intent(this, AlarmsListActivity::class.java)) }

    }


    private fun initSmsReceivedService() {
        val smsListener = SMSReceiverImpl()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsListener, intentFilter)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val cal = PersianCalendar.getInstance()
        binding.clockTv.text = cal.time
        binding.dateTv.text = "${cal.persianWeekDayStr} - ${cal.iranianDay} ${cal.iranianMonthName}"

        val lastMessageData = Setting.lastMessageDate
        if (lastMessageData != 0L) {
            val lastMessageDateCal = PersianCalendar.getInstance(lastMessageData / 1000)
            binding.lastMessageDateTv.text =
                "${lastMessageDateCal.persianWeekDayStr} - ${lastMessageDateCal.iranianDay} ${lastMessageDateCal.iranianMonthName} ${lastMessageDateCal.time}"
        } else {
            binding.lastMessageDateTv.text = getString(R.string.notSendAnyMessage)
        }

    }

}
