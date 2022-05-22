package com.rymo.felfel.features.alarm.alarmDetails.dialog

import android.content.Context
import android.view.View
import android.widget.RadioGroup
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import com.rymo.felfel.common.getTimeFromLong
import kotlinx.android.synthetic.main.set_delay_dialog.*
import timber.log.Timber

class SetDelayDialog(mContext: Context, private val delay: Long, private val onResult: (Long) -> Unit) :
    BaseBottomSheetDialog(mContext, R.layout.set_delay_dialog) {

    override fun initDialog(view: View) {
        init()
    }


    private fun init() {

        var time: Long = (delay / 60) / 1000
        Timber.e("Delay set -> $time")
        if (time < 1) {
            secondRb.isChecked = true
            time = delay / 1000
        } else {
            minuteRb.isChecked = true
        }

        timeEt.setText(if (time == 0L) "" else time.toString())

        submitBtn.setOnClickListener {
            Timber.e("Delay : inout -> ${timeEt.text.toString()}")
            onResult.invoke(
                if (timeEt.text.toString().isEmpty()) {
                    Timber.e("output -> 0")
                    0L
                } else {
                    if (secondRb.isChecked) {
                        Timber.e("Delay : output -> ${(timeEt.text.toString().toLong() * 1000)}")
                        (timeEt.text.toString().toLong() * 1000)
                    } else {
                        Timber.e("Delay : output -> ${(timeEt.text.toString().toLong() * 1000 * 60)}")
                        (timeEt.text.toString().toLong() * 1000 * 60)
                    }
                }
            )
            dismiss()
        }

        cancelBtn.setOnClickListener { dismiss() }

    }

}
