package com.rymo.felfel.features.reports.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.reportFile
import com.rymo.felfel.common.share
import com.rymo.felfel.databinding.FragmentNotSendMessagesUserReportBinding
import com.rymo.felfel.features.common.dialog.ShareFileDialog
import com.rymo.felfel.features.common.dialog.ShareType
import com.rymo.felfel.features.detailsReport.DetailsReportActivity
import com.rymo.felfel.features.detailsReport.enum.ReportType
import com.rymo.felfel.features.reports.ReportsActivity
import com.rymo.felfel.features.reports.ReportsViewModel
import com.rymo.felfel.features.reports.adapter.ReportListAdapter

class NotSendMessagesUserReportFragment : Base.BaseFragment() {

    private lateinit var binding: FragmentNotSendMessagesUserReportBinding
    private val mSharedViewModel: ReportsViewModel by lazy { (activity as ReportsActivity).mViewModel }

    companion object {
        fun newInstance(): NotSendMessagesUserReportFragment {
            val args = Bundle()
            val fragment = NotSendMessagesUserReportFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()), R.layout.fragment_not_send_messages_user_report, container, false
        )
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
            ShareFileDialog(requireContext()) {
                when (it) {
                    ShareType.TEXT -> {
                        reportFile(requireContext(),smsMessageSendTime.date,smsMessageSendTime.time,"txt"){
                            it.share(requireContext())
                        }
                    }
                    ShareType.EXCEL -> {
                        reportFile(requireContext(),smsMessageSendTime.date,smsMessageSendTime.time,"xls") {
                            it.share(requireContext())
                        }
                    }
                }
            }.show(childFragmentManager,"SHARE")
        } else {
            DetailsReportActivity.start(
                requireActivity(),
                ReportType.NO_REPLAY,
                smsMessageSendTime.date,
                smsMessageSendTime.time,
                smsMessageSendTime.textSms
            )
        }
    }


}
