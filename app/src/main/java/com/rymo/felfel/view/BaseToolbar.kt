package com.rymo.felfel.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.rymo.felfel.R
import com.rymo.felfel.databinding.ViewToolbarBinding

class BaseToolbar(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var binding: ViewToolbarBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_toolbar, this, true)

    var onBackButtonClickListener: View.OnClickListener? = null
        set(value) {
            field = value
            binding.backBtn.setOnClickListener(onBackButtonClickListener)
        }

    init {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.BaseToolbar)
            val title = a.getString(R.styleable.BaseToolbar_bt_title)
            val showBack = a.getBoolean(R.styleable.BaseToolbar_bt_showBack, true)
            val tintColor = a.getColor(R.styleable.BaseToolbar_bt_tint, context.resources.getColor(R.color.black))
            val backgroundColor = a.getColor(R.styleable.BaseToolbar_bt_background, context.resources.getColor(R.color.white))
            if (!title.isNullOrEmpty())
                binding.toolbarTitleTv.text = title
            else
                binding.toolbarTitleTv.text = ""

            if (!showBack) {
                binding.backBtn.visibility = View.GONE
                var params = binding.toolbarTitleTv.layoutParams as FrameLayout.LayoutParams
                params.setMargins(0, 0, 46, 0)
            }

            binding.toolbarTitleTv.setTextColor(tintColor)
            binding.backBtn.setColorFilter(tintColor)
            binding.toolbarLyt.setBackgroundColor(backgroundColor)

            a.recycle()
        }

    }

    fun setTitle(title: String) {
        binding.toolbarTitleTv.text = title
    }
}
