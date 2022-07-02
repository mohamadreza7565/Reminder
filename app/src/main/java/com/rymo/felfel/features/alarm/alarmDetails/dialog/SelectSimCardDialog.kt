package com.rymo.felfel.features.alarm.alarmDetails.dialog

import android.content.Context
import android.telephony.SubscriptionInfo
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.SimUtil
import com.rymo.felfel.features.alarm.alarmDetails.dialog.adapter.SimCardListAdapter
import kotlinx.android.synthetic.main.select_sim_card_dialog.*

class SelectSimCardDialog(
    mContext: Context,
    private val simCardSelected: Int,
    private val onResult: (id: Int) -> Unit
) : BaseBottomSheetDialog(mContext, R.layout.select_sim_card_dialog) {


    override fun initDialog(view: View) {
        init()
    }

    private fun init() {
        initList()
        initClick()
    }

    private fun initClick() {
        toolbarView.onBackButtonClickListener = View.OnClickListener { dismiss() }
    }

    private fun initList() {
        simsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = simCardListAdapter()
        }

        (simsRv.adapter as SimCardListAdapter).sims = SimUtil.getSimCount()
    }

    private fun simCardListAdapter() = SimCardListAdapter(requireContext(), simCardSelected, onSimClick = {
        onResult.invoke(it.subscriptionId)
        dismiss()
    }, onApiSelect = {
        onResult.invoke(Constants.API_ID)
        dismiss()
    })

}
