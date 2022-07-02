package com.rymo.felfel.features.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.*
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.database.createDataBaseInstance
import com.rymo.felfel.database.dao.SmsMessageDao
import com.rymo.felfel.databinding.ActivityMainBinding
import com.rymo.felfel.features.aboutMe.AboutMeActivity
import com.rymo.felfel.features.alarm.alarmList.AlarmsListActivity
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.contacts.ContactsActivity
import com.rymo.felfel.features.group.GroupActivity
import com.rymo.felfel.features.main.dialog.AddAutoReplyTextDialog
import com.rymo.felfel.features.reports.ReportsActivity
import com.rymo.felfel.features.workshop.WorkshopActivity
import com.rymo.felfel.model.Contact
import com.rymo.felfel.receiver.sms.SMSReceiverImpl
import com.rymo.felfel.view.scroll.ObservableScrollViewCallbacks
import com.rymo.felfel.view.scroll.ScrollState
import timber.log.Timber


class MainActivity : Base.BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDatabase: RoomAppDatabase
    private lateinit var smsMessageDao: SmsMessageDao

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
        initDatabase()
        initSmsReceivedService()
        initClick()
        initList()
        initScroll()
    }

    private fun initScroll() {
        binding.headerLyt.post {
            val videoCoverImageView = binding.headerLyt
            val coverIvHeight = binding.headerLyt.height
            binding.scrollView.addScrollViewCallbacks(object : ObservableScrollViewCallbacks {
                override fun onScrollChanged(
                    scrollY: Int,
                    firstScroll: Boolean,
                    dragging: Boolean
                ) {
                    videoCoverImageView.translationY = scrollY.toFloat() / 2
                }

                override fun onDownMotionEvent() {
                }

                override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) {
                }

            })
        }
    }

    private fun initDatabase() {
        appDatabase = createDataBaseInstance(this)
        smsMessageDao = appDatabase.smsMessageDao()
    }

    private fun initList() {

        binding.rvInbox.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ContactListAdapter(false) { position, longClick, contact ->

            }
        }
        val contacts: MutableList<Contact> = ArrayList()
        smsMessageDao.getInbox().convertListToMutableList().forEach {
            contacts.add(Contact(0, it.from, it.name, it.companyName, it.message, date = it.date))
        }
        (binding.rvInbox.adapter as ContactListAdapter).contacts = contacts

    }

    private fun initClick() {

        binding.llContact.setOnClickListener { ContactsActivity.start(this) }

        binding.llAlarm.setOnClickListener { AlarmsListActivity.start(this) }

        binding.reportBtn.setOnClickListener { ReportsActivity.start(this) }

        binding.llGroup.setOnClickListener { GroupActivity.start(this) }

        binding.aboutMeBtn.setOnClickListener { AboutMeActivity.start(this) }

        binding.autoReplyBtn.setOnClickListener { AddAutoReplyTextDialog(this).show(supportFragmentManager, "ADD_AUTO_TEXT_MESSAGE") }

        binding.llWorkshop.setOnClickListener { WorkshopActivity.start(this) }
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
