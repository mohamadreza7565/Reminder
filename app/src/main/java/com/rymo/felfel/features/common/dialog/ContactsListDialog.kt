package com.rymo.felfel.features.common.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.BaseBottomSheetDialog
import com.rymo.felfel.common.gone
import com.rymo.felfel.common.visible
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.common.adapter.GroupListAdapter
import com.rymo.felfel.features.contacts.ContactsViewModel
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.Group
import kotlinx.android.synthetic.main.contacts_list_dialog.*
import kotlinx.android.synthetic.main.view_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ContactsListDialog(
    context: Context,
    private val contacts: MutableList<Contact>,
    private val groups: MutableList<Group>,
    private val onSubmit: (MutableList<Contact>, MutableList<Group>) -> Unit
) : BaseBottomSheetDialog(context, R.layout.contacts_list_dialog) {

    private val mViewModel: ContactsViewModel by viewModel()

    override fun initDialog(view: View) {
        init()
    }

    private fun init() {
        provideContacts()
    }

    private fun initData() {

        Timber.e("Default Contacts -> ${contacts}")
        mViewModel.contacts.forEach { _viewModelContact ->
            contacts.find { _contact -> _contact.id == _viewModelContact.id }?.let {
                _viewModelContact.selected = it.selected
            }
        }

        mViewModel.groups.forEach { _viewModelGroup ->
            groups.find { _group -> _group.id == _viewModelGroup.id }?.let {
                _viewModelGroup.selected = it.selected
            }
        }

        changeGroupSelected()

    }

    private fun provideContacts() {
        mViewModel.contactListLiveData.observe(this) {
            Timber.e("Observe Contacts : ${it.size}")
            if (!it.isNullOrEmpty()) {
                mViewModel.contacts.clear()
                mViewModel.contacts.addAll(it)
            }
            provideGroups()
        }
    }

    private fun provideGroups() {
        mViewModel.groupListLiveData.observe(this) {
            if (!it.isNullOrEmpty()) {
                mViewModel.groups.clear()
                mViewModel.groups.addAll(it)
            }
            initData()
            initContactsList()
            initClick()
        }

    }

    private fun initGroupList() {

        contactsBtn.isSelected = false
        groupBtn.isSelected = true

        contactsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter()
        }

        (contactsRv.adapter as GroupListAdapter).groups = mViewModel.groups
        groupError()
    }

    private fun initClick() {
        submitBtn.setOnClickListener {
            onSubmit.invoke(mViewModel.contacts, mViewModel.groups)
            dismiss()
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }

        contactsBtn.setOnClickListener {
            if (!contactsBtn.isSelected) {
                initContactsList()
            }
        }

        groupBtn.setOnClickListener {
            if (!groupBtn.isSelected) {
                initGroupList()
            }
        }
    }

    private fun initContactsList() {

        contactsBtn.isSelected = true
        groupBtn.isSelected = false

        contactsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter()
        }

        (contactsRv.adapter as ContactListAdapter).contacts = mViewModel.contacts
        contactsError()
    }

    private fun contactsError() {
        Timber.e("Contacts : ${mViewModel.contacts.size}")
        if (mViewModel.contacts.isNullOrEmpty()) {
            errorIv.setImageResource(R.drawable.ic_empty_folder)
            errorTv.text = getString(R.string.contactIsEmpty)
            retryBtn.gone()
            errorView.visible()
        } else {
            errorView.gone()
        }
    }

    private fun groupError() {
        Timber.e("Group Contacts : ${mViewModel.contacts.size}")
        if (mViewModel.groups.isNullOrEmpty()) {
            errorIv.setImageResource(R.drawable.ic_empty_folder)
            errorTv.text = getString(R.string.groupIsEmpty)
            retryBtn.gone()
            errorView.visible()
        } else {
            errorView.gone()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun contactAdapter() = ContactListAdapter { position, longClick, contact ->
        mViewModel.contacts[position].selected = !mViewModel.contacts[position].selected
        (contactsRv.adapter as ContactListAdapter).notifyDataSetChanged()
        changeGroupSelected()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun groupAdapter() = GroupListAdapter { position, longClick, group ->

        mViewModel.groups[position].selected = !mViewModel.groups[position].selected

        mViewModel.contactsGroup(group).forEach { _contactGroupId ->
            mViewModel.contacts.find { _contact -> _contact.id == _contactGroupId }?.let {
                it.selected = true
            }
        }

        (contactsRv.adapter as GroupListAdapter).notifyDataSetChanged()
    }

    private fun changeGroupSelected(){

        mViewModel.groups.forEach { _group ->
            val contactsGroup = mViewModel.getContactsGroup(_group.id)
            var selected = true
            contactsGroup.forEach {
                mViewModel.contactListLiveData.value?.find { _contact -> _contact.id == it.id }?.let { _contact ->
                    if (!_contact.selected)
                        selected = false
                }
            }
            _group.selected = selected
        }
    }


}
