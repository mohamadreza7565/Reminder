package com.rymo.felfel.features.splash

import com.rymo.felfel.R
import com.rymo.felfel.common.Base
import com.rymo.felfel.configuration.AlarmApplication
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.model.Group

class SplashViewModel(
    private val appDatabase: RoomAppDatabase
) : Base.BaseViewModel() {

    private val contactDao = appDatabase.contactDao()


}
