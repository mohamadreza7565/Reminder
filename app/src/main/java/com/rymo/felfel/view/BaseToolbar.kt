package com.rymo.felfel.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.rymo.felfel.R
import com.rymo.felfel.databinding.ViewToolbarBinding

class BaseToolbar(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var binding : ViewToolbarBinding =
        ViewToolbarBinding.inflate(LayoutInflater.from(context), this)

    var onBackButtonClickListener: View.OnClickListener? = null
        set(value) {
            field = value
            binding.backBtn.setOnClickListener(onBackButtonClickListener)
        }

    init {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.BaseToolbar)
            val title = a.getString(R.styleable.BaseToolbar_bt_title)
            val showBack = a.getBoolean(R.styleable.BaseToolbar_bt_showBack,true)
            if (!title.isNullOrEmpty())
                binding.toolbarTitleTv.text = title

            if (!showBack) {
                binding.backBtn.visibility = View.GONE
                var params = binding.toolbarTitleTv.layoutParams as FrameLayout.LayoutParams
                params.setMargins(0,0,46,0)
            }

            a.recycle()
        }

    }

    fun setTitle(title: String) {
        binding.toolbarTitleTv.text = title
    }
}