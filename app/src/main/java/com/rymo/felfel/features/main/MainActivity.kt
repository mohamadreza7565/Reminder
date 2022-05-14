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

     fun addAlarm(v:View){
        startActivity(Intent(this, AlarmsListActivity::class.java))
    }

    fun addContact(v:View){
        startActivity(Intent(this, ContactsActivity::class.java))
    }

    private fun init() {
        initSmsReceivedService()
    }



    private fun initSmsReceivedService() {
        val smsListener = SMSReceiverImpl()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsListener, intentFilter)
    }

}
