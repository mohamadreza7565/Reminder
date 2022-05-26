package com.rymo.felfel.features.main.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import com.rymo.felfel.data.preferences.Setting
import kotlinx.android.synthetic.main.add_auto_reply_text_dialog.*

class AddAutoReplyTextDialog(mContext: Context) : BaseBottomSheetDialog(mContext, R.layout.add_auto_reply_text_dialog) {

    override fun initDialog(view: View) {
        initClick()
    }

    private fun initClick() {

        submitBtn.setOnClickListener {
            Setting.autoReplayMessage = messageEt.text.toString()
            dismiss()
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }

    }

}
