package com.rymo.felfel.features.common.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.confirm_dialog.*

class ConfirmDialog(context: Context, private val title: String, private val onSubmit: () -> Unit) :
    BaseBottomSheetDialog(context, R.layout.confirm_dialog) {

    override fun initDialog(view: View) {
        init()
    }

    private fun init() {
        titleTv.text = title
        initClick()
    }

    private fun initClick() {

        submitBtn.setOnClickListener {
            onSubmit()
            dismiss()
        }

        cancelBtn.setOnClickListener { dismiss() }
    }

}
