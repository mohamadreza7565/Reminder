package com.rymo.felfel.data.preferences

import android.content.Context
import com.google.gson.Gson
import com.rymo.felfel.configuration.AlarmApplication

open class FatherPreferences {

    lateinit var mContext: Context
    lateinit var mGson :Gson

    init {
        mContext = AlarmApplication.instance!!
        mGson = Gson()
    }
}
