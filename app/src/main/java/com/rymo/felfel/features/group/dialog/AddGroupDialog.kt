package com.rymo.felfel.features.group.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.Group
import kotlinx.android.synthetic.main.add_group_dialog.*

class AddGroupDialog(
    context: Context, private val onSubmit: (contact: Group) -> Unit
) : BaseBottomSheetDialog(context, R.layout.add_group_dialog) {

    override fun initDialog(view: View) {
        init()
    }

    private fun init() {

        submitBtn.setOnClickListener {
            validation()
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }

    }

    private fun validation() {
        when {
            groupNameEt.text.toString().isEmpty() -> groupNameEt.error = ""
            else -> {
                val group = Group(name = groupNameEt.text.toString(),)
                onSubmit.invoke(group)
                dismiss()
            }
        }
    }

}
