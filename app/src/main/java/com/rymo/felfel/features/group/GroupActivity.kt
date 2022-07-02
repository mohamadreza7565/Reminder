package com.rymo.felfel.features.group

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
import com.rymo.felfel.databinding.ActivityGroupBinding
import com.rymo.felfel.features.common.adapter.ContactListAdapter
import com.rymo.felfel.features.common.adapter.GroupListAdapter
import com.rymo.felfel.features.common.dialog.ConfirmDialog
import com.rymo.felfel.features.contacts.ContactsActivity
import com.rymo.felfel.features.contacts.ContactsViewModel
import com.rymo.felfel.features.contacts.dialog.AddContactDialog
import com.rymo.felfel.features.contacts.dialog.OptionContactDialog
import com.rymo.felfel.features.contactsGroup.ContactsGroupActivity
import com.rymo.felfel.features.group.dialog.AddGroupDialog
import com.rymo.felfel.features.group.dialog.OptionGroupDialog
import com.rymo.felfel.features.group.dialog.OptionGroupType
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.Group
import kotlinx.android.synthetic.main.activity_contacts.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupActivity : Base.BaseActivity() {

    private val mViewModel: ContactsViewModel by viewModel()
    private lateinit var binding: ActivityGroupBinding

    companion object {
        fun start(mContext: Context) {
            Intent(mContext, GroupActivity::class.java).also {
                startActivity(mContext, it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group)
        init()
    }

    private fun init() {
        initClick()
        initList()
        provideGroupList()
    }

    private fun provideGroupList() {
        mViewModel.groupListLiveData.observe(this) {
            setErrorLayout(
                mustShow = it.isNullOrEmpty(),
                textError = getString(R.string.groupIsEmpty),
                imageError = R.drawable.ic_empty_folder
            )

            (binding.groupsRv.adapter as GroupListAdapter).groups = it

        }
    }

    private fun initList() {
        binding.groupsRv.apply {
            layoutManager = LinearLayoutManager(this@GroupActivity)
            adapter = groupAdapter()
        }
    }

    private fun groupAdapter() = GroupListAdapter { position, longClick, group ->
        if (longClick) {
            optionGroupDialog(group, position)
        } else {
            ContactsGroupActivity.start(this, group.id)
        }
    }

    private fun optionGroupDialog(group: Group, position: Int) =
        OptionGroupDialog(this) {
            when (it) {
                OptionGroupType.DELETE -> {
                    var content = getString(R.string.doYouWantDeleteGroup)
                    confirmDialog(content) {
                        mViewModel.deleteGroup(group)
                        (binding.groupsRv.adapter as GroupListAdapter).remove(position)
                        mViewModel.groupListLiveData.postValue(mViewModel.groupListLiveData.value)
                    }
                }
                OptionGroupType.EDIT -> {
                    addGroupDialog(group)
                }
            }
        }.show(supportFragmentManager, "OPTION_GROUP")


    private fun confirmDialog(title: String, onSubmit: () -> Unit) = ConfirmDialog(this, title) {
        onSubmit()
    }.show(supportFragmentManager, "CONFIRM_DIALOG")

    private fun initClick() {
        binding.fab.setOnClickListener {
            addGroupDialog()
        }

        toolbarView.onBackButtonClickListener = View.OnClickListener { onBackPressed() }

    }

    private fun addGroupDialog(group: Group? = null) =
        AddGroupDialog(this, group) {
            if (group == null) {
                mViewModel.addGroup(it)
            } else {
                it.id = group.id
                mViewModel.editGroup(it)
            }
            mViewModel.getGroups()
        }.show(supportFragmentManager, "ADD_GROUP")


}
