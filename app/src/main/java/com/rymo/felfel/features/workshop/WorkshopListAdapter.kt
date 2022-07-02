package com.rymo.felfel.features.workshop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rymo.felfel.R
import com.rymo.felfel.common.PersianCalendar
import com.rymo.felfel.common.gone
import com.rymo.felfel.configuration.AlarmApplication
import com.rymo.felfel.databinding.ContactListItemBinding
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.WorkshopModel

class WorkshopListAdapter(
    private val canBeEdit: Boolean = true,
    private val onClick: ((position: Int, longClick: Boolean, contact: WorkshopModel) -> Unit)
) :
    RecyclerView.Adapter<WorkshopListAdapter.ViewHolder>() {

    var contacts: MutableList<WorkshopModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun update(position: Int, contact: WorkshopModel) {
        contacts[position] = contact
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        contacts.removeAt(position)
        notifyDataSetChanged()
    }

    fun add(contact: WorkshopModel) {
        contacts.add(contact)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkshopListAdapter.ViewHolder {
        val binding: ContactListItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.contact_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkshopListAdapter.ViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    inner class ViewHolder(binding: ContactListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val itemBinding = binding

        fun bind(contact: WorkshopModel) {

            itemBinding.nameTv.text = contact.name
            itemBinding.phoneTv.text = contact.phone
            itemBinding.companyLyt.gone()
            itemBinding.replayLyt.gone()

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
