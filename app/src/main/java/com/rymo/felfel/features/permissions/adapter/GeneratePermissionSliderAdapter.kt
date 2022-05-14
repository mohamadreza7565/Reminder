package com.rymo.felfel.features.permissions.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rymo.felfel.features.permissions.fragment.GeneratePermissionFragment
import com.rymo.felfel.features.permissions.model.GeneratePermissionModel

class GeneratePermissionSliderAdapter(
    private val activity: AppCompatActivity,
    private val permissions: MutableList<GeneratePermissionModel>,
    private val doRequest: ((String) -> Unit)
) :
    FragmentStateAdapter(activity), GeneratePermissionFragment.OnRequestPermission {

    override fun getItemCount(): Int = permissions.size

    override fun createFragment(position: Int): Fragment =
        GeneratePermissionFragment.newInstance(permissions[position], this)

    override fun doRequestPermission(permissionName: String) {
        doRequest.invoke(permissionName)
    }

}
