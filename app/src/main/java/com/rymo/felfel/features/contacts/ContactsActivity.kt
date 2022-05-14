package com.rymo.felfel.features.contacts

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.startActivity
import com.rymo.felfel.databinding.ActivityContactsBinding
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.common.dialog.ConfirmDialog
import com.rymo.felfel.features.contacts.dialog.AddContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactType
import com.rymo.felfel.features.reports.ReportsActivity
import com.rymo.felfel.model.Contact
import kotlinx.android.synthetic.main.activity_contacts.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactsActivity : Base.BaseActivity() {

    private val mViewModel: ContactsViewModel by viewModel()
    private lateinit var binding: ActivityContactsBinding


    companion object {
        fun start(mContext: Context) {
            Intent(mContext, ContactsActivity::class.java).also {
                startActivity(mContext, it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts)
        init()
    }

    private fun init() {
        initClick()
        initList()
        provideContactList()
    }

    private fun provideContactList() {
        mViewModel.contactListLiveData.observe(this) {

            setErrorLayout(
                mustShow = it.isNullOrEmpty(),
                textError = getString(R.string.contactIsEmpty),
                imageError = R.drawable.ic_empty_folder
            )

            (binding.contactsRv.adapter as ContactListAdapter).contacts = it

        }
    }

    private fun initList() {
        binding.contactsRv.apply {
            layoutManager = LinearLayoutManager(this@ContactsActivity)
            adapter = contactAdapter()
        }
    }

    private fun contactAdapter() = ContactListAdapter { position, longClick, contact ->
        if (longClick) {
            optionDialog(contact, position).show(supportFragmentManager, "CONTACT_OPTION")
        } else {

        }
    }

    private fun optionDialog(contact: Contact, position: Int) = OptionContactDialog(this) {
        when {
            it == OptionContactType.DELETE -> {
                var content = "${getString(R.string.deleteFromAlarmContactWhenContactDeleted)}\n"
                content += getString(R.string.doYouWantDeleteContact)
                confirmDialog(content) {
                    mViewModel.deleteContact(contact)
                    mViewModel.contactListLiveData.value!!.removeAt(position)
                    mViewModel.contactListLiveData.postValue(mViewModel.contactListLiveData.value)
                }
            }
        }
    }


    private fun confirmDialog(title: String, onSubmit: () -> Unit) = ConfirmDialog(this, title) {
        onSubmit()
    }.show(supportFragmentManager, "CONFIRM_DIALOG")

    private fun initClick() {
        binding.fab.setOnClickListener {
            addContactDialog().show(supportFragmentManager, "ADD_CONTACT")
        }

        toolbarView.onBackButtonClickListener = View.OnClickListener { onBackPressed() }

    }

    private fun addContactDialog() =
        AddContactDialog(this) {
            mViewModel.addContact(it)
            mViewModel.contactListLiveData.value!!.add(it)
            mViewModel.contactListLiveData.postValue(mViewModel.contactListLiveData.value)
        }


}
