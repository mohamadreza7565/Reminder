package com.rymo.felfel.features.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.RequestPermission
import com.rymo.felfel.common.SimUtil
import com.rymo.felfel.common.setLocale
import com.rymo.felfel.configuration.globalInject
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.databinding.ActivitySplashBinding
import com.rymo.felfel.features.alarm.DynamicThemeHandler
import com.rymo.felfel.features.main.MainActivity
import com.rymo.felfel.features.permissions.GeneratePermissionsActivity
import com.rymo.felfel.repo.ExcelRepoImpl
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity : Base.BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val dynamicThemeHandler: DynamicThemeHandler by globalInject()
    private val mViewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(dynamicThemeHandler.defaultTheme())
        setLocale(this, Setting.language!!)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        object : CountDownTimer(2000, 2000) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {

                if (RequestPermission.newInstance(this@SplashActivity)
                        .checkPermissionList(
                            arrayOf(
                                RequestPermission.STORAGE, RequestPermission.SMS
                            )
                        )
                ) {
                    MainActivity.start(this@SplashActivity)
                } else {
                    GeneratePermissionsActivity.start(this@SplashActivity)
                }
                finish()

            }

        }.start()

    }
}
