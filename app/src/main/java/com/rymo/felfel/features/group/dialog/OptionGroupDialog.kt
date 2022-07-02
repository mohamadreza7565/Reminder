package com.rymo.felfel.features.group.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.option_group_dialog.*

class OptionGroupDialog(context: Context, private val onSubmit: (OptionGroupType) -> Unit) :
    BaseBottomSheetDialog(context, R.layout.option_group_dialog) {

    override fun initDialog(view: View) {
        init()
    }

    private fun init() {
        initClick()
    }

    private fun initClick() {

        deleteBtn.setOnClickListener {
            onSubmit.invoke(OptionGroupType.DELETE)
            dismiss()
        }

        editBtn.setOnClickListener {
            onSubmit.invoke(OptionGroupType.EDIT)
            dismiss()
        }

    }

}


public enum class OptionGroupType {
    DELETE, EDIT
}

