package com.rymo.felfel.features.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rymo.felfel.R
import com.rymo.felfel.common.gone
import com.rymo.felfel.configuration.AlarmApplication
import com.rymo.felfel.databinding.ContactListItemBinding
import com.rymo.felfel.model.Contact

class ContactListAdapter(
    private val canBeEdit: Boolean = true,
    private val onClick: ((position: Int, longClick: Boolean, contact: Contact) -> Unit)
) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    var contacts: MutableList<Contact> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun update(position: Int, contact: Contact) {
        contacts[position] = contact
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        contacts.removeAt(position)
        notifyDataSetChanged()
    }

    fun add(contact: Contact) {
        contacts.add(contact)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListAdapter.ViewHolder {
        val binding: ContactListItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.contact_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactListAdapter.ViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    inner class ViewHolder(binding: ContactListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val itemBinding = binding

        fun bind(contact: Contact) {

            itemBinding.nameTv.text = contact.nameAndFamily
            itemBinding.phoneTv.text = contact.phone
            itemBinding.restaurantNameTv.text = contact.companyName
            if (contact.companyName.isEmpty()){
                itemBinding.restaurantNameTitleTv.gone()
            }

            if (contact.selected) {
                itemBinding.itemCv.setCardBackgroundColor(AlarmApplication.instance!!.resources.getColor(R.color.redLight2))
            } else {
                itemBinding.itemCv.setCardBackgroundColor(AlarmApplication.instance!!.resources.getColor(R.color.white))
            }

            itemBinding.root.setOnClickListener {
                onClick.invoke(adapterPosition, false, contact)
            }

            itemBinding.root.setOnLongClickListener {
                onClick.invoke(adapterPosition, true, contact)
                true
            }

        }

    }

}
