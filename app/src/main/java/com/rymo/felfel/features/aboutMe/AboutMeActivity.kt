package com.rymo.felfel.features.aboutMe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.common.startActivity
import com.rymo.felfel.databinding.ActivityAboutMeBinding
import kotlinx.android.synthetic.main.activity_about_me.*

class AboutMeActivity : Base.BaseActivity() {

    private lateinit var binding : ActivityAboutMeBinding

    companion object{
        fun start(meActivity: Activity){
            Intent(meActivity,AboutMeActivity::class.java).also {
                startActivity(meActivity,it)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_about_me)
        init()
    }

    private fun init(){
        initClick()
    }

    private fun initClick() {

        gmailBtn.setOnClickListener {
            sendMail()
        }

        websiteBtn.setOnClickListener {
            openWebsite()
        }

        binding.toolbarView.onBackButtonClickListener =
            View.OnClickListener { onBackPressed() }

    }

    private fun openWebsite() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.seniorandroid.ir"))
        startActivity(browserIntent)
    }

    private fun sendMail() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this

        intent.putExtra(Intent.EXTRA_EMAIL, "engineer.it93@gmail.com")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
