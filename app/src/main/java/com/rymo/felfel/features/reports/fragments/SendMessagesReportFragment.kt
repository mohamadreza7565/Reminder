package com.rymo.felfel.features.reports.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.databinding.FragmentSendMessagesReportBinding
import com.rymo.felfel.features.detailsReport.DetailsReportActivity
import com.rymo.felfel.features.detailsReport.enum.ReportType
import com.rymo.felfel.features.reports.ReportsActivity
import com.rymo.felfel.features.reports.ReportsViewModel
import com.rymo.felfel.features.reports.adapter.ReportListAdapter

class SendMessagesReportFragment : Base.BaseFragment() {

    private lateinit var binding: FragmentSendMessagesReportBinding
    private val mSharedViewModel: ReportsViewModel by lazy { (activity as ReportsActivity).mViewModel }

    companion object {
        fun newInstance(): SendMessagesReportFragment {
            val args = Bundle()
            val fragment = SendMessagesReportFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_send_messages_report, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initList()
        provideList()
    }

    private fun provideList() {
        mSharedViewModel.allSmsMessagesTimeLiveData.observe(requireActivity()) {
            setErrorLayout(
                mustShow = it.isNullOrEmpty(),
                imageError = R.drawable.ic_empty_folder,
                textError = getString(R.string.notSendAnyAutoMessage)
            )
            (binding.reportsRv.adapter as ReportListAdapter).list = it
        }
    }

    private fun initList() {
        binding.reportsRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = reportListAdapter()
        }
    }

    private fun reportListAdapter() = ReportListAdapter { longClick, smsMessageSendTime ->
        if (longClick) {

        } else {
            DetailsReportActivity.start(
                requireActivity(),
                ReportType.SEND_MESSAGE,
                smsMessageSendTime.date,
                smsMessageSendTime.time,
                smsMessageSendTime.textSms
            )
        }
    }


}
