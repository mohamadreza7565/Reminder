package com.rymo.felfel.features.common.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.dialog_share.*

class ShareFileDialog(mContext: Context, private val onResult: (ShareType) -> Unit) :
    BaseBottomSheetDialog(mContext, R.layout.dialog_share) {

    override fun initDialog(view: View) {

        excelBtn.setOnClickListener {
            onResult.invoke(ShareType.EXCEL)
            dismiss()
        }

        textBtn.setOnClickListener {
            onResult.invoke(ShareType.TEXT)
            dismiss()
        }

    }
}

public enum class ShareType {
    EXCEL, TEXT
}
