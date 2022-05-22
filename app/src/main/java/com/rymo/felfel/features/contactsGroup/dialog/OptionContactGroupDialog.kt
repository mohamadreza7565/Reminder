package com.rymo.felfel.features.contactsGroup.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.option_contact_group_dialog.*

class OptionContactGroupDialog(context: Context, private val onSubmit: (OptionContactGroupType) -> Unit) :
    BaseBottomSheetDialog(context, R.layout.option_contact_group_dialog) {

    override fun initDialog(view: View) {
        init()
    }

    private fun init() {
        initClick()
    }

    private fun initClick() {

        deleteBtn.setOnClickListener {
            onSubmit.invoke(OptionContactGroupType.DELETE)
            dismiss()
        }

    }

}

public enum class OptionContactGroupType {
    DELETE, EDIT
}
