package com.rymo.felfel.features.contacts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.*
import com.rymo.felfel.databinding.ActivityContactsBinding
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.common.dialog.ConfirmDialog
import com.rymo.felfel.features.contacts.dialog.AddContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactType
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

        fun start(mContext: Activity, selectable: Boolean, groupId: Long) {
            Intent(mContext, ContactsActivity::class.java).apply {
                putExtra(Constants.KEY_EXTRA_ID, groupId)
                putExtra(Constants.KEY_EXTRA_TYPE, selectable)
            }.also {
                mContext.startActivityForResult(it, Constants.REQ_SELECT_CONTACTS_GROUP)
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
        initView()
        initList()
        provideContactList()
        observeBackup()
    }

    private fun initView() {
        if (intent.getBooleanExtra(Constants.KEY_EXTRA_TYPE, false)) {
            binding.fab.text = getString(R.string.submit)
            binding.toolbarView.setTitle(getString(R.string.addContactToGroup))
        }
    }

    private fun observeBackup() {
        mViewModel.exportLiveData().observe(this) {
            when {

            }
        }
    }

    private fun provideContactList() {
        mViewModel.contactListLiveData.observe(this) {

            setErrorLayout(
                mustShow = it.isNullOrEmpty(),
                textError = getString(R.string.contactIsEmpty),
                imageError = R.drawable.ic_empty_folder
            )

            if (!intent.getBooleanExtra(Constants.KEY_EXTRA_TYPE, false))
                (binding.contactsRv.adapter as ContactListAdapter).contacts = it
            else {
                selectItemSelected()
            }

        }
    }

    private fun selectItemSelected() {
        mViewModel.getContactsGroup(intent.getLongExtra(Constants.KEY_EXTRA_ID, 0))

        mViewModel.contactsGroupListLiveData.observe(this) {
            mViewModel.contactListLiveData.value?.forEach { _viewModelContact ->
                it!!.find { _contact -> _contact.id == _viewModelContact.id }?.let {
                    _viewModelContact.selected = true
                }
            }
            mViewModel.contactListLiveData.value?.let {
                (binding.contactsRv.adapter as ContactListAdapter).contacts = it
            }
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
            if (intent.getBooleanExtra(Constants.KEY_EXTRA_TYPE, false)) {
                mViewModel.contactListLiveData.value!![position].selected = !contact.selected
                (binding.contactsRv.adapter as ContactListAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun optionDialog(contact: Contact, position: Int) = OptionContactDialog(this) {
        when (it) {
            OptionContactType.DELETE -> {
                var content = "${getString(R.string.deleteFromAlarmContactWhenContactDeleted)}\n"
                content += getString(R.string.doYouWantDeleteContact)
                confirmDialog(content) {
                    mViewModel.deleteContact(contact)
                    mViewModel.contactListLiveData.value!!.removeAt(position)
                    mViewModel.contactListLiveData.postValue(mViewModel.contactListLiveData.value)
                    mViewModel.exportContact()
                }
            }
            OptionContactType.EDIT -> {
                addContactDialog(contact)
            }
        }
    }

    private fun confirmDialog(title: String, onSubmit: () -> Unit) = ConfirmDialog(this, title) {
        onSubmit()
    }.show(supportFragmentManager, "CONFIRM_DIALOG")

    private fun initClick() {
        binding.fab.setOnClickListener {
            if (intent.getBooleanExtra(Constants.KEY_EXTRA_TYPE, false)) {
                addContactsSelectedToGroup()
                setResult(Activity.RESULT_OK, Intent())
                finish()
            } else {
                addContactDialog()
            }
        }

        toolbarView.onBackButtonClickListener = View.OnClickListener { onBackPressed() }

    }

    private fun addContactsSelectedToGroup() {
        mViewModel.removeAllContactFromGroup(intent.getLongExtra(Constants.KEY_EXTRA_ID, 0))
        mViewModel.addContactsToGroup(intent.getLongExtra(Constants.KEY_EXTRA_ID, 0))
    }

    private fun addContactDialog(contact: Contact? = null) =
        AddContactDialog(this, contact = contact) {
            if (contact == null) {
                mViewModel.addContact(it)
            } else {
                it.id = contact.id
                mViewModel.editContact(it)
            }
            mViewModel.getContacts()
            mViewModel.exportContact()
        }.show(supportFragmentManager, "ADD_CONTACT")


}
