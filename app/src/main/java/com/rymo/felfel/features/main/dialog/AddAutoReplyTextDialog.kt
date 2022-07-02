package com.rymo.felfel.features.main.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.SimUtil
import com.rymo.felfel.common.toast
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.features.alarm.alarmDetails.dialog.SelectSimCardDialog
import kotlinx.android.synthetic.main.add_auto_reply_text_dialog.*

class AddAutoReplyTextDialog(mContext: Context) : BaseBottomSheetDialog(mContext, R.layout.add_auto_reply_text_dialog) {

    private var simCardId: Int = Setting.autoReplayMessageSimId
    private val sims = SimUtil.getSimCount()

    override fun initDialog(view: View) {
        initClick()
    }

    private fun initClick() {

        Setting.autoReplayMessage?.let {
            messageEt.setText(it)
        }

        submitBtn.setOnClickListener {
            when {
                messageEt.text.toString().isNotEmpty() && simCardId == -10 -> toast("سیمکارت مبدا را انتخاب کنید")
                else -> {
                    Setting.autoReplayMessage = messageEt.text.toString()
                    Setting.autoReplayMessageSimId = simCardId
                    dismiss()
                }
            }
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }

        if (simCardId == Constants.API_ID) {
            simCardBtn.text = "از طریق سرور"
        } else {
            val sim = sims.find { simCardId == it.subscriptionId }
            if (sim != null)
                simCardBtn.text = sim.carrierName
        }

        simCardBtn.setOnClickListener {
            SelectSimCardDialog(requireContext(), simCardId) { simIdSelected ->
                if (simIdSelected == Constants.API_ID) {
                    simCardId = simIdSelected
                    simCardBtn.text = "از طریق سرور"
                } else {
                    val sim = sims.find { simIdSelected == it.subscriptionId }
                    if (sim != null) {
                        simCardBtn.text = sim.carrierName
                        simCardId = sim.subscriptionId
                    } else {
                        val defaultSim = sims.first()
                        simCardBtn.text = defaultSim.carrierName
                        simCardId = defaultSim.subscriptionId
                    }
                }
            }.show(childFragmentManager, "SELECT_SIM")
        }

    }

}
