package com.rymo.felfel.features.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.databinding.ActivityContactsBinding
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.contacts.dialog.AddContactDialog
import kotlinx.android.synthetic.main.activity_contacts.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactsActivity : Base.BaseActivity() {

    private val mViewModel: ContactsViewModel by viewModel()
    private lateinit var binding: ActivityContactsBinding

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

        } else {

        }
    }

    private fun initClick() {
        binding.fab.setOnClickListener {
            addContactDialog().show(supportFragmentManager, "ADD_CONTACT")
        }

//        toolbarView.onBackButtonClickListener = View.OnClickListener { onBackPressed() }

    }

    private fun addContactDialog() =
        AddContactDialog(this) {
            mViewModel.addContact(it)
            mViewModel.contactListLiveData.value!!.add(it)
            mViewModel.contactListLiveData.postValue(mViewModel.contactListLiveData.value)
        }


}
