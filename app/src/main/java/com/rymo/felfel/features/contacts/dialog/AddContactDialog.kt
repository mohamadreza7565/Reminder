package com.rymo.felfel.features.contacts.dialog

import android.content.Context
import android.view.View
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import com.rymo.felfel.common.gone
import com.rymo.felfel.model.Contact
import kotlinx.android.synthetic.main.add_contact_dialog.*

class AddContactDialog(
    context: Context,
    private val showCompany: Boolean = true,
    private val contact: Contact?,
    private val onSubmit: (contact: Contact) -> Unit
) : BaseBottomSheetDialog(context, R.layout.add_contact_dialog) {

    override fun initDialog(view: View) {
        init()
    }

    private fun init() {

        contact?.let {
            customerNameEt.setText(it.nameAndFamily)
            customerPhoneEt.setText(it.phone)
            customerCompanyNameEt.setText(it.companyName)
        }

        if (!showCompany) {
            customerCompanyNameEtLyt.gone()
        }

        submitBtn.setOnClickListener {
            validation()
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }

    }

    private fun validation() {
        when {
            customerNameEt.text.toString().isEmpty() -> customerNameEt.error = getString(R.string.inputContactName)
            customerPhoneEt.text.toString().isEmpty() -> customerPhoneEt.error = getString(R.string.inputPhoneNumber)
            customerPhoneEt.text.toString().length < 11 && !customerPhoneEt.text.toString().startsWith("09") ->
                customerPhoneEt.error = getString(R.string.invalidPhoneNumber)
            else -> {
                val contact = Contact(
                    0,
                    customerPhoneEt.text.toString(),
                    customerNameEt.text.toString(),
                    customerCompanyNameEt.text.toString()
                )

                onSubmit.invoke(contact)
                dismiss()
            }
        }
    }

}
