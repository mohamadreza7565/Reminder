package com.rymo.felfel.features.alarm

import com.rymo.felfel.R
import com.rymo.felfel.configuration.Prefs
import com.rymo.felfel.stores.modify

class DynamicThemeHandler(
    private val prefs: Prefs,
) {
  private val light = "light"
  private val dark = "dark"
  private val synthwave = "synthwave"
  private val deusex = "deusex"
  private val deepblue = "deepblue"

  private val themes: Map<String, List<Int>> =
      mapOf(
          light to
              listOf(
//                  R.style.DefaultLightTheme,
                  R.style.AlarmAlertFullScreenLightTheme,
                  R.style.TimePickerDialogFragmentLight,
              ),
          dark to
              listOf(
//                  R.style.DefaultDarkTheme,
                  R.style.AlarmAlertFullScreenDarkTheme,
                  R.style.TimePickerDialogFragmentDark,
              )
      )

  init {
    prefs.theme.modify { currentTheme ->
      when (currentTheme) {
        in themes.keys -> currentTheme
        else -> dark
      }
    }
  }

  @JvmOverloads
  fun defaultTheme(theme: String? = null): Int = themes.getValue(theme ?: prefs.theme.value)[0]
  fun alertTheme(): Int = themes.getValue(prefs.theme.value)[0]
  fun pickerTheme(): Int = themes.getValue(prefs.theme.value)[1]
}
