package com.rymo.felfel.features.reports.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rymo.felfel.R
import com.rymo.felfel.databinding.ReportListItemBinding
import com.rymo.felfel.model.SmsMessageModel
import com.rymo.felfel.model.SmsMessageSendTime

class ReportListAdapter(private val onClick: (longClick: Boolean, SmsMessageSendTime) -> Unit) :
    RecyclerView.Adapter<ReportListAdapter.ViewHolder>() {

    var list: MutableList<SmsMessageSendTime> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun remove(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListAdapter.ViewHolder {
        val binding: ReportListItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.report_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportListAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemBinding: ReportListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        val binding = itemBinding

        @SuppressLint("SetTextI18n")
        fun bind(sms: SmsMessageSendTime) {

            binding.timeTv.text = "${sms.date} \n ${sms.time}"

            binding.root.setOnClickListener {
                onClick.invoke(false,sms)
            }

            binding.root.setOnLongClickListener {
                onClick.invoke(true,sms)
                true
            }

        }

    }

}
