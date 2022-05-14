package com.rymo.felfel.data.preferences

import com.google.gson.reflect.TypeToken
import com.rymo.felfel.common.Constants
import com.rymo.felfel.common.GlobalPreferences
import org.koin.java.KoinJavaComponent
import java.lang.reflect.Type

object UserPref {


    var currentUserId: String?
        get() {
            return GlobalPreferences.getString(
                Constants.SHP_CURRENT_USER_ID, Constants.NO_STRING_DATA
            )
        }
        set(userId) {
            GlobalPreferences.setString(Constants.SHP_CURRENT_USER_ID, userId)
        }

    fun isUserLogin(): Boolean {
        val profiles = currentUserId
        return profiles != ""
    }

    fun clearPreferences() {
        GlobalPreferences.clearPreferences()
    }
}