package com.rymo.felfel.features.detailsReport

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.startActivity
import com.rymo.felfel.common.visible
import com.rymo.felfel.databinding.ActivityDetailsReportBinding
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.detailsReport.enum.ReportType
import com.rymo.felfel.features.reports.ReportsViewModel
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.SmsMessageModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsReportActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDetailsReportBinding
    private val mViewModel: ReportsViewModel by viewModel()
    private lateinit var reportType: ReportType
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var textMessage: String

    companion object {
        fun start(mActivity: Activity, type: ReportType, date: String, time: String,textMessage : String) {
            Intent(mActivity, DetailsReportActivity::class.java).apply {
                putExtra(Constants.KEY_EXTRA_TYPE, type)
                putExtra(Constants.KEY_EXTRA_DATE, date)
                putExtra(Constants.KEY_EXTRA_TIME, time)
                putExtra(Constants.KEY_EXTRA_DATA, textMessage)
            }.also {
                startActivity(mActivity, it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details_report)
        init()
    }

    private fun init() {
        initBundle()
        initData()
        initClick()
    }

    private fun initClick() {
        binding.toolbarView.onBackButtonClickListener = View.OnClickListener { onBackPressed() }
    }

    private fun initData() {
        when {
            reportType == ReportType.SEND_MESSAGE -> {
                provideAllMessages()
            }
            reportType == ReportType.NO_REPLAY -> {
                provideNoReplayMessages()
            }
            reportType == ReportType.DO_REPLAY -> {
                provideDoReplayMessages()
            }
        }
    }

    private fun provideAllMessages() {
        mViewModel.getAllSendMessages(date, time)
        mViewModel.allSmsMessagesLiveData.observe(this) {
            initList(it)
        }
    }


    private fun provideNoReplayMessages() {
        mViewModel.getNoReplaySendMessages(date, time)
        mViewModel.noReplaySmsMessagesLiveData.observe(this) {
            initList(it)
        }
    }

    private fun provideDoReplayMessages() {
        mViewModel.getDoReplaySendMessages(date, time)
        mViewModel.doReplaySmsMessagesLiveData.observe(this) {
            initList(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initList(list: MutableList<SmsMessageModel>) {
        binding.reportsRv.apply {
            layoutManager = LinearLayoutManager(this@DetailsReportActivity)
            adapter = contactListAdapter()
        }

        binding.timeTv.text = "$date  -  $time"
        binding.messageTv.text = textMessage

        val contacts: MutableList<Contact> = ArrayList()
        list.forEach {
            contacts.add(Contact(0, it.phoneNumber, it.contactName, it.companyName, it.replayText))
        }

        (binding.reportsRv.adapter as ContactListAdapter).contacts = contacts

        if (list.isNullOrEmpty())
            binding.emptyTv.visible()

    }

    private fun contactListAdapter() = ContactListAdapter(false) { position, longClick, contact ->

    }

    private fun initBundle() {
        reportType = intent.getSerializableExtra(Constants.KEY_EXTRA_TYPE) as ReportType
        date = intent.getStringExtra(Constants.KEY_EXTRA_DATE).toString()
        time = intent.getStringExtra(Constants.KEY_EXTRA_TIME).toString()
        textMessage = intent.getStringExtra(Constants.KEY_EXTRA_DATA).toString()
    }
}
