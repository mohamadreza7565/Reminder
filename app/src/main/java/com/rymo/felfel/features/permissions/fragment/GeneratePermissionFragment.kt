package com.rymo.felfel.features.permissions.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.RequestPermission
import com.rymo.felfel.databinding.FragmentGeneratePermissionsBinding
import com.rymo.felfel.features.permissions.model.GeneratePermissionModel
import java.lang.IllegalStateException

class GeneratePermissionFragment(var onRequestPermission: OnRequestPermission) :
    Base.BaseFragment() {

    private lateinit var binding: FragmentGeneratePermissionsBinding
    private var permission: GeneratePermissionModel? = null

    companion object {

        fun newInstance(
            perrmission: GeneratePermissionModel,
            onRequestPermission: OnRequestPermission
        ): GeneratePermissionFragment {
            val args = Bundle()
            val fragment = GeneratePermissionFragment(onRequestPermission)
            args.putParcelable(Constants.KEY_EXTRA_DATA, perrmission)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_generate_permissions, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initBundle()
        initView()
        initClick()
    }

    private fun initBundle() {
        permission = arguments?.getParcelable(Constants.KEY_EXTRA_DATA)
    }

    private fun initView() {

        binding.descriptionTv.text = permission?.permissionDescription

        binding.permissionIv.setAnimation(permission!!.permissionImage)

    }

    private fun initClick() {
        binding.btnAccess.setOnClickListener {
            onRequestPermission.doRequestPermission(
                permission!!.permissionName
            )
        }
    }

    interface OnRequestPermission {
        fun doRequestPermission(permissionName: String)
    }


}
