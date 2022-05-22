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
import com.rymo.felfel.databinding.GroupListItemBinding
import com.rymo.felfel.model.Contact
import com.rymo.felfel.model.Group

class GroupListAdapter(
    private val canBeEdit: Boolean = true,
    private val onClick: ((position: Int, longClick: Boolean, group : Group) -> Unit)
) :
    RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {

    var groups: MutableList<Group> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun update(position: Int, group: Group) {
        groups[position] = group
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        groups.removeAt(position)
        notifyDataSetChanged()
    }

    fun add(group: Group) {
        groups.add(group)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListAdapter.ViewHolder {
        val binding: GroupListItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.group_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupListAdapter.ViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size

    inner class ViewHolder(binding: GroupListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val itemBinding = binding

        fun bind(group: Group) {

            itemBinding.nameTv.text = group.name

            if (group.selected) {
                itemBinding.itemCv.setCardBackgroundColor(AlarmApplication.instance!!.resources.getColor(R.color.redLight2))
            } else {
                itemBinding.itemCv.setCardBackgroundColor(AlarmApplication.instance!!.resources.getColor(R.color.white))
            }

            itemBinding.root.setOnClickListener {
                onClick.invoke(adapterPosition, false, group)
            }

            itemBinding.root.setOnLongClickListener {
                onClick.invoke(adapterPosition, true, group)
                true
            }

        }

    }

}
