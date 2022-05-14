package com.rymo.felfel.features.reports.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.databinding.FragmentNotSendMessagesUserReportBinding
import com.rymo.felfel.databinding.FragmentSendMessagesReportBinding

class NotSendMessagesUserReportFragment : Base.BaseFragment() {

    private lateinit var binding : FragmentNotSendMessagesUserReportBinding

   companion object{
       fun newInstance(): NotSendMessagesUserReportFragment {
           val args = Bundle()
           val fragment = NotSendMessagesUserReportFragment()
           fragment.arguments = args
           return fragment
       }
   }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),R.layout.fragment_not_send_messages_user_report,container,false)
        return binding.root
    }

}
