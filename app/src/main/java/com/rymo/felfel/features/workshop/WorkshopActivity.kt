package com.rymo.felfel.features.workshop

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.startActivity
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.databinding.ActivityWorkshopBinding
import com.rymo.felfel.features.alarm.alarmDetails.dialog.SelectSimCardDialog
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.common.dialog.ConfirmDialog
import com.rymo.felfel.features.contacts.ContactsViewModel
import com.rymo.felfel.features.contacts.dialog.AddContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactType
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.WorkshopModel
import kotlinx.android.synthetic.main.activity_contacts.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WorkshopActivity : Base.BaseActivity() {

    private val mViewModel: ContactsViewModel by viewModel()
    private lateinit var binding: ActivityWorkshopBinding

    companion object {
        fun start(mContext: Context) {
            Intent(mContext, WorkshopActivity::class.java).also {
                startActivity(mContext, it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_workshop)
        init()
    }


    private fun init() {
        initClick()
        initList()
        provideContactList()
    }

    private fun provideContactList() {
        mViewModel.getWorkshopList()
        mViewModel.workshopListLiveData.observe(this) {

            setErrorLayout(
                mustShow = it.isNullOrEmpty(),
                textError = getString(R.string.contactIsEmpty),
                imageError = R.drawable.ic_empty_folder
            )

            (binding.contactsRv.adapter as WorkshopListAdapter).contacts = it

        }
    }


    private fun initList() {
        binding.contactsRv.apply {
            layoutManager = LinearLayoutManager(this@WorkshopActivity)
            adapter = contactAdapter()
        }
    }

    private fun contactAdapter() = WorkshopListAdapter { position, longClick, contact ->
        if (longClick) {
            optionDialog(contact, position).show(supportFragmentManager, "CONTACT_OPTION")
        } else {

        }
    }

    private fun optionDialog(contact: WorkshopModel, position: Int) = OptionContactDialog(this) {
        when (it) {
            OptionContactType.DELETE -> {
                var content = "${getString(R.string.doYouWantDeleteContact)}"
                confirmDialog(content) {
                    mViewModel.deleteWorkshop(contact)
                    mViewModel.workshopListLiveData.value!!.removeAt(position)
                    mViewModel.workshopListLiveData.postValue(mViewModel.workshopListLiveData.value)
                    mViewModel.exportWorkshop()
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
            addContactDialog()
        }

        binding.toolbarView.onBackButtonClickListener = View.OnClickListener { onBackPressed() }

        binding.simCardBtn.setOnClickListener {
            SelectSimCardDialog(this, Setting.workShopSimCard) {
                Setting.workShopSimCard = it
            }.show(supportFragmentManager,"SIM_CARD_SELECT_DIALOG")
        }

    }


    private fun addContactDialog(workshopModel: WorkshopModel? = null) {

        var contact: Contact? = null
        workshopModel?.let {
            contact = Contact(id = 0, phone = workshopModel.phone, nameAndFamily = workshopModel.name)
        }

        AddContactDialog(this, false, contact) {
            if (workshopModel == null) {
                val newWorkshop = WorkshopModel(0L, it.nameAndFamily, it.phone)
                mViewModel.addWorkshopContact(newWorkshop)
            } else {
                val newWorkshop = WorkshopModel(workshopModel.id, it.nameAndFamily, it.phone)
                mViewModel.editWorkshop(newWorkshop)
            }
            mViewModel.getWorkshopList()
            mViewModel.exportWorkshop()
        }.show(supportFragmentManager, "ADD_CONTACT")
    }

}
