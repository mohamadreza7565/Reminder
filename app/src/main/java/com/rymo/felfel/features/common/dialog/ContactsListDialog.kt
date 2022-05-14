package com.rymo.felfel.features.common.dialog

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.model.Contact
import kotlinx.android.synthetic.main.contacts_list_dialog.*

class ContactsListDialog(
    context: Context,
    private val contacts: MutableList<Contact>,
    private val onSubmit: (MutableList<Contact>) -> Unit
) : BaseBottomSheetDialog(context, R.layout.contacts_list_dialog) {

    override fun initDialog(view: View) {
        init()
    }

    private fun init() {
        initList()
        initClick()
    }

    private fun initClick() {
        submitBtn.setOnClickListener {
            onSubmit.invoke(contacts)
            dismiss()
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun initList() {
        contactsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter()
        }

        (contactsRv.adapter as ContactListAdapter).contacts = contacts

    }

    private fun contactAdapter() = ContactListAdapter { position, longClick, contact ->
        contacts[position].selected = !contacts[position].selected
        (contactsRv.adapter as ContactListAdapter).notifyDataSetChanged()
    }


}
