package com.rymo.felfel.features.reports

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.rymo.felfel.R
import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.GlobalFragmentPagerAdapter
import com.rymo.felfel.common.ZoomOutPageTransformer
import com.rymo.felfel.common.startActivity
import com.rymo.felfel.databinding.ActivityReportsBinding
import kotlinx.android.synthetic.main.include_tab.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding
    private var mTabAdapter: GlobalFragmentPagerAdapter? = null
    private lateinit var mInflater: LayoutInflater
    private var TAB_COUNT = 3

    companion object {
        fun start(mContext: Context) {
            Intent(mContext, ReportsActivity::class.java).also {
                startActivity(mContext, it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reports)
        init()

    }

    private fun init() {
        initTab()
        initClick()
    }

    private fun initClick() {
        binding.toolbarView.onBackButtonClickListener = View.OnClickListener { onBackPressed() }
    }

    private fun initTab() {

        mInflater = LayoutInflater.from(this)
        mTabAdapter = GlobalFragmentPagerAdapter(
            supportFragmentManager, TAB_COUNT,
            GlobalFragmentPagerAdapter.REPORTS
        )

        binding.vViewPager.apply {
            offscreenPageLimit = TAB_COUNT
            setPageTransformer(true, ZoomOutPageTransformer())
            currentItem = Constants.SEND_MESSAGES_REPORT
            adapter = mTabAdapter
        }

        tabLayout.setupWithViewPager(binding.vViewPager)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        tabLayout.getTabAt(Constants.SEND_MESSAGES_REPORT)?.customView = getString(R.string.messagesSend).newTab()
        tabLayout.getTabAt(Constants.GET_MESSAGES_REPORT)?.customView = getString(R.string.messagesGet).newTab()
        tabLayout.getTabAt(Constants.NOT_GET_MESSAGES_USERS_REPORT)?.customView = getString(R.string.messagesNotSendUser).newTab()
    }


    private fun String.newTab(): AppCompatTextView {
        val tabTextView: AppCompatTextView = mInflater.inflate(R.layout.item_tab, null)
            .findViewById(R.id.item_tab_txt_title)

        tabTextView.text = this
        return tabTextView
    }

}
