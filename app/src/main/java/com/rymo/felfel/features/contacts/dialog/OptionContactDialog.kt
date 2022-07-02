package com.rymo.felfel.features.contacts.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.option_contact_dialog.*

class OptionContactDialog(context: Context, private val onSubmit: (OptionContactType) -> Unit) :
    BaseBottomSheetDialog(context, R.layout.option_contact_dialog) {

    override fun initDialog(view: View) {
        init()
    }

    private fun init() {
        initClick()
    }

    private fun initClick() {

        deleteBtn.setOnClickListener {
            onSubmit.invoke(OptionContactType.DELETE)
            dismiss()
        }

        editBtn.setOnClickListener {
            onSubmit.invoke(OptionContactType.EDIT)
            dismiss()
        }

    }

}

public enum class OptionContactType {
    DELETE, EDIT
}
