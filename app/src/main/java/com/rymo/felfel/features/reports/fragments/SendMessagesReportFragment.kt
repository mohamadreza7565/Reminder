package com.rymo.felfel.features.reports.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.databinding.FragmentSendMessagesReportBinding

class SendMessagesReportFragment : Base.BaseFragment() {

    private lateinit var binding: FragmentSendMessagesReportBinding

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

}
