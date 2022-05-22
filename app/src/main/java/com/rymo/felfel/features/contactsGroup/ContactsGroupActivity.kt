package com.rymo.felfel.features.contactsGroup

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.*
import com.rymo.felfel.databinding.ActivityContactsGroupBinding
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.common.dialog.ConfirmDialog
import com.rymo.felfel.features.contacts.ContactsActivity
import com.rymo.felfel.features.contacts.ContactsViewModel
import com.rymo.felfel.features.contacts.dialog.AddContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactType
import com.rymo.felfel.features.contactsGroup.dialog.OptionContactGroupDialog
import com.rymo.felfel.features.contactsGroup.dialog.OptionContactGroupType
import com.rymo.felfel.model.Contact
import kotlinx.android.synthetic.main.activity_contacts.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactsGroupActivity : Base.BaseActivity() {

    private lateinit var binding: ActivityContactsGroupBinding
    private val mViewModel: ContactsViewModel by viewModel()

    companion object {
        fun start(mActivity: Activity, groupId: Long) {
            Intent(mActivity, ContactsGroupActivity::class.java).apply {
                putExtra(Constants.KEY_EXTRA_ID, groupId)
            }.also {
                startActivity(mActivity, it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts_group)
        init()
    }

    private fun init() {
        initClick()
        initList()
        provideContactList()
    }


    private fun provideContactList() {
        mViewModel.getContactsGroup(intent.getLongExtra(Constants.KEY_EXTRA_ID, 0))
        mViewModel.contactsGroupListLiveData.observe(this) {

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
            layoutManager = LinearLayoutManager(this@ContactsGroupActivity)
            adapter = contactAdapter()
        }
    }

    private fun contactAdapter() = ContactListAdapter { position, longClick, contact ->
        if (longClick) {
            optionDialog(contact, position).show(supportFragmentManager, "CONTACT_OPTION")
        } else {

        }
    }

    private fun optionDialog(contact: Contact, position: Int) = OptionContactGroupDialog(this) {
        when {
            it == OptionContactGroupType.DELETE -> {
                var content = getString(R.string.doYouWantDeleteContactFromGroup)
                confirmDialog(content) {
                    mViewModel.deleteContactFromGroup(contact.id, intent.getLongExtra(Constants.KEY_EXTRA_ID, 0))
                    mViewModel.contactsGroupListLiveData.value!!.removeAt(position)
                    mViewModel.contactsGroupListLiveData.postValue(mViewModel.contactsGroupListLiveData.value)
                }
            }
        }
    }

    private fun confirmDialog(title: String, onSubmit: () -> Unit) = ConfirmDialog(this, title) {
        onSubmit()
    }.show(supportFragmentManager, "CONFIRM_DIALOG")

    private fun initClick() {
        binding.fab.setOnClickListener {
            ContactsActivity.start(this, true, intent.getLongExtra(Constants.KEY_EXTRA_ID, 0))
        }

        toolbarView.onBackButtonClickListener = View.OnClickListener { onBackPressed() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQ_SELECT_CONTACTS_GROUP) {
            mViewModel.getContactsGroup(intent.getLongExtra(Constants.KEY_EXTRA_ID, 0))
        }
    }

}
