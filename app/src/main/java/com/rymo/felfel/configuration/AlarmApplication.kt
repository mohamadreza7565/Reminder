/*
 * Copyright (C) 2012 Yuriy Kulikov yuriy.kulikov.87@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rymo.felfel.configuration

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.view.ViewConfiguration
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.rymo.felfel.R
import com.rymo.felfel.alert.BackgroundNotifications
import com.rymo.felfel.background.AlertServicePusher
import com.rymo.felfel.bugreports.BugReporter
import com.rymo.felfel.configuration.AlarmApplicationInit.startOnce
import com.rymo.felfel.createNotificationChannels
import com.rymo.felfel.database.createDataBaseInstance
import com.rymo.felfel.model.AlarmValue
import com.rymo.felfel.model.Alarms
import com.rymo.felfel.model.AlarmsScheduler
import com.rymo.felfel.features.alarm.ScheduledReceiver
import com.rymo.felfel.features.alarm.ToastPresenter
import com.rymo.felfel.features.alarm.alarmDetails.AlarmDetailsViewModel
import com.rymo.felfel.features.alarm.alarmList.AlarmListViewModel
import com.rymo.felfel.features.contacts.ContactsViewModel
import com.rymo.felfel.features.permissions.GeneratePermissionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class AlarmApplication : MultiDexApplication() {


    override fun onCreate() {
        startOnce(this)
        instance = this
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        @JvmStatic
        fun startOnce(application: Application) {
            application.startOnce()
        }

        @get:Synchronized
        var instance: AlarmApplication? = null
    }
}

private object AlarmApplicationInit {
    private val started = AtomicBoolean(false)

    @SuppressLint("SoonBlockedPrivateApi")
    fun Application.startOnce() {
        if (started.getAndSet(true)) {
            return
        }

        runCatching {
            ViewConfiguration::class
                .java
                .getDeclaredField("sHasPermanentMenuKey")
                .apply { isAccessible = true }
                .setBoolean(ViewConfiguration.get(this), false)
        }

        val koin = startKoin(applicationContext)

        koin.get<BugReporter>().attachToMainThread(this)

        // must be after sContainer
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        // TODO make it lazy
        koin.get<ScheduledReceiver>().start()
        koin.get<ToastPresenter>().start()
        koin.get<AlertServicePusher>()
        koin.get<BackgroundNotifications>()

        createNotificationChannels()

        // must be started the last, because otherwise we may loose intents from it.
        val alarmsLogger = koin.logger("Alarms")
        koin.get<Alarms>().start()
        alarmsLogger.debug { "Started alarms, SDK is " + Build.VERSION.SDK_INT }
        // start scheduling alarms after all alarms have been started
        koin.get<AlarmsScheduler>().start()

        with(koin.get<Store>()) {
            // register logging after startup has finished to avoid logging( O(n) instead of O(n log n) )
            alarms()
                .distinctUntilChanged()
                .map { it.toSet() }
                .startWith(emptySet<AlarmValue>())
                .buffer(2, 1)
                .map { (prev, next) -> next.minus(prev).map { it.toString() } }
                .distinctUntilChanged()
                .subscribe { lines -> lines.forEach { alarmsLogger.debug { it } } }
        }

    }
}
