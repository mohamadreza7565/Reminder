package com.rymo.felfel.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rymo.felfel.common.enum.PeekHeightStyle
import com.rymo.felfel.R

abstract class BaseBottomSheetDialog(var mContext: Context,
                                     private val layoutID: Int,
                                     private val mPeekHeightStyle: PeekHeightStyle = PeekHeightStyle.DEFAULT_SCREEN) : BottomSheetDialogFragment() {

    private var mPeekHeight = getHeightScreenSize(mContext)
    private var currView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(layoutID, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currView = view
        initDialog(view)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            (it as BottomSheetDialog?)?.apply {
                (findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?)?.let { fl ->
                    setPeakHeightInView(fl)
                }
            }
        }

        return dialog
    }

    private fun setPeakHeightInView(bottomSheet: ViewGroup) {
        val mBehavior = BottomSheetBehavior.from(bottomSheet)
        when (mPeekHeightStyle) {
            PeekHeightStyle.FULL_SCREEN -> mBehavior.peekHeight = mPeekHeight
            PeekHeightStyle.HALF_SCREEN -> mBehavior.peekHeight = ((mPeekHeight / 2.5).toInt())
            else -> mBehavior.peekHeight = mPeekHeight
        }
    }

    protected abstract fun initDialog(view: View)
}