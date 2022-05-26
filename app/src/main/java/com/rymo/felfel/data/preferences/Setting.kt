package com.rymo.felfel.data.preferences

import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.GlobalPreferences


object Setting {

    var deviceId: String?
        get() {
            return GlobalPreferences.getString(
                Constants.SHP_DEVICE_ID_ID, Constants.NO_STRING_DATA
            )
        }
        set(deviceId) {
            GlobalPreferences.setString(Constants.SHP_DEVICE_ID_ID, deviceId)
        }

    var fcmToken: String?
        get() {
            return GlobalPreferences.getString(
                Constants.SHP_FCM_TOKEN, Constants.NO_STRING_DATA
            )
        }
        set(fcmToken) {
            GlobalPreferences.setString(Constants.SHP_FCM_TOKEN, fcmToken)
        }

    var language: String?
        get() {
            return GlobalPreferences.getString(
                Constants.SHP_LANGUAGE, Constants.LANGUAGE_DEFAULT
            )
        }
        set(language) {
            GlobalPreferences.setString(Constants.SHP_LANGUAGE, language)
        }

    var theme: String?
        get() {
            return GlobalPreferences.getString(
                Constants.SHP_THEME, Constants.THEME_LIGHT
            )
        }
        set(theme) {
            GlobalPreferences.setString(Constants.SHP_THEME, theme)
        }

    var firstOpenAlarmList: Boolean
        get() {
            return GlobalPreferences.getBoolean(
                Constants.SHP_FIRST_OPEN_ALARM, true
            )
        }
        set(firstOpenAlarmList) {
            GlobalPreferences.setBoolean(Constants.SHP_FIRST_OPEN_ALARM, firstOpenAlarmList)
        }

    var firstOpenApp: Boolean
        get() {
            return GlobalPreferences.getBoolean(
                Constants.SHP_FIRST_OPEN_APP, true
            )
        }
        set(firstOpenApp) {
            GlobalPreferences.setBoolean(Constants.SHP_FIRST_OPEN_APP, firstOpenApp)
        }


    var autoReplayMessage: String?
        get() {
            return GlobalPreferences.getString(
                Constants.SHP_AUTO_REPLAY_MESSAGE, ""
            )
        }
        set(autoReplayMessage) {
            GlobalPreferences.setString(Constants.SHP_AUTO_REPLAY_MESSAGE, autoReplayMessage)
        }

    var lastMessageDate: Long
        get() {
            return GlobalPreferences.getLong(
                Constants.SHP_LAST_MESSAGE_DATE, 0
            )
        }
        set(lastMessageDate) {
            GlobalPreferences.setLong(Constants.SHP_LAST_MESSAGE_DATE, lastMessageDate)
        }

    fun clearPreferences() {
        GlobalPreferences.clearPreferences()
    }
}
