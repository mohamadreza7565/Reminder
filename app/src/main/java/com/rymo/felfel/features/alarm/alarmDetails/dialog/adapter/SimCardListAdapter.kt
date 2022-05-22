package com.rymo.felfel.features.alarm.alarmDetails.dialog.adapter

import android.telephony.SubscriptionInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rymo.felfel.R
import com.rymo.felfel.configuration.AlarmApplication
import com.rymo.felfel.databinding.GroupListItemBinding
import com.rymo.felfel.databinding.SimCardListItemBinding
import com.rymo.felfel.model.Group

class SimCardListAdapter(
    private val simCardSelected: Int,
    private val onClick: ((simCard: SubscriptionInfo) -> Unit)
) :
    RecyclerView.Adapter<SimCardListAdapter.ViewHolder>() {

    var sims: MutableList<SubscriptionInfo> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun update(position: Int, sim: SubscriptionInfo) {
        sims[position] = sim
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        sims.removeAt(position)
        notifyDataSetChanged()
    }

    fun add(sim: SubscriptionInfo) {
        sims.add(sim)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimCardListAdapter.ViewHolder {
        val binding: SimCardListItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.sim_card_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimCardListAdapter.ViewHolder, position: Int) {
        holder.bind(sims[position])
    }

    override fun getItemCount(): Int = sims.size

    inner class ViewHolder(binding: SimCardListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val itemBinding = binding

        fun bind(sim: SubscriptionInfo) {

            itemBinding.nameTv.text = sim.carrierName

            itemBinding.simIv.setImageBitmap(sim.createIconBitmap(AlarmApplication.instance))

            if (simCardSelected == sim.subscriptionId) {
                itemBinding.itemCv.setCardBackgroundColor(AlarmApplication.instance!!.resources.getColor(R.color.redLight2))
            } else {
                itemBinding.itemCv.setCardBackgroundColor(AlarmApplication.instance!!.resources.getColor(R.color.white))
            }

            itemBinding.root.setOnClickListener {
                onClick.invoke(sim)
            }


        }

    }

}
