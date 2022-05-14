package com.rymo.felfel.features.permissions

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rymo.felfel.R
import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.RequestPermission
import com.rymo.felfel.common.startActivity
import com.rymo.felfel.databinding.ActivityGeneratePermissionsBinding
import com.rymo.felfel.features.main.MainActivity
import com.rymo.felfel.features.permissions.adapter.GeneratePermissionSliderAdapter
import com.rymo.felfel.features.permissions.model.GeneratePermissionModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class GeneratePermissionsActivity : AppCompatActivity() {

    private val mViewModel : GeneratePermissionViewModel by viewModel()
    private val permissionList: MutableList<GeneratePermissionModel> = ArrayList()
    private lateinit var binding: ActivityGeneratePermissionsBinding

    companion object {
        fun start(mActivity: Activity) {
            Intent(mActivity, GeneratePermissionsActivity::class.java).also {
                startActivity(mActivity, it)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_permissions)
        init()
    }

    private fun init() {
        checkPermission()
    }

    private fun checkPermission() {
        if (!RequestPermission.newInstance(this).checkSmsPermission(false)) {
            permissionList.add(
                GeneratePermissionModel(
                    RequestPermission.SMS, getString(R.string.smsPermissionDescription),
                    R.raw.sms_lottie
                )
            )
        }

        if (!RequestPermission.newInstance(this).checkStoragePermission(false)) {
            permissionList.add(
                GeneratePermissionModel(
                    RequestPermission.STORAGE, getString(R.string.storagePermissionDescription),
                    R.raw.storage_lottie
                )
            )
        }

        initViewPager()

    }

    private fun initViewPager() {
        val sliderAdapter = GeneratePermissionSliderAdapter(this, permissionList) {
            doRequest(it)
        }

        binding.sliderViewPager.apply {
            adapter = sliderAdapter
        }
    }

    private fun doRequest(permissionName: String) {
        when (permissionName) {
            RequestPermission.SMS -> {
                if (RequestPermission.newInstance(this).checkSmsPermission(true)) {
                    changeSlide(permissionList)
                }
            }
            RequestPermission.STORAGE -> {
                if (RequestPermission.newInstance(this).checkStoragePermission(true)) {
                    changeSlide(permissionList)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                if (checkPermissionRequestCode(requestCode)) {
                    changeSlide(permissionList)
                    return
                }
            }
        }

    }

    private fun changeSlide(permissions: MutableList<GeneratePermissionModel>) {
        if (binding.sliderViewPager.currentItem == permissions.size - 1) {
            MainActivity.start(this)
            finish()
        } else {
            binding.sliderViewPager.setCurrentItem(
                binding.sliderViewPager.currentItem + 1, true
            )
        }
    }

    private fun checkPermissionRequestCode(requestCode: Int): Boolean {
        if (requestCode == Constants.REQ_SMS_PERMISSIONS) {
            return true
        }

        if (requestCode == Constants.REQ_STORAGE_PERMISSIONS) {
            return true
        }

        return false
    }

}
