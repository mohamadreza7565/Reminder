package com.rymo.felfel.common

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*
import android.preference.PreferenceManager




fun setLocale(context: Context, lng: String = "fa") {
    val lang = PreferenceManager.getDefaultSharedPreferences(context).getString("locale", lng)
    val newLocale = Locale(lang)
    Locale.setDefault(newLocale)
    val config = Configuration()
    config.locale = newLocale

    val res: Resources = context.resources
    res.updateConfiguration(config, res.displayMetrics)
}