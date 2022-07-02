package com.rymo.felfel.configuration

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.PowerManager
import org.koin.androidx.viewmodel.dsl.viewModel
import android.os.Vibrator
import android.telephony.TelephonyManager
import com.rymo.felfel.alert.BackgroundNotifications
import com.rymo.felfel.background.AlertServicePusher
import com.rymo.felfel.background.KlaxonPlugin
import com.rymo.felfel.background.PlayerWrapper
import com.rymo.felfel.bugreports.BugReporter
import com.rymo.felfel.database.createDataBaseInstance
import com.rymo.felfel.interfaces.IAlarmsManager
import com.rymo.felfel.logger.Logger
import com.rymo.felfel.logger.LoggerFactory
import com.rymo.felfel.logger.loggerModule
import com.rymo.felfel.model.AlarmCore
import com.rymo.felfel.model.AlarmCoreFactory
import com.rymo.felfel.model.AlarmSetter
import com.rymo.felfel.model.AlarmStateNotifier
import com.rymo.felfel.model.Alarms
import com.rymo.felfel.model.AlarmsScheduler
import com.rymo.felfel.model.Calendars
import com.rymo.felfel.model.ContainerFactory
import com.rymo.felfel.model.IAlarmsScheduler
import com.rymo.felfel.persistance.DatabaseQuery
import com.rymo.felfel.persistance.PersistingContainerFactory
import com.rymo.felfel.persistance.RetryingDatabaseQuery
import com.rymo.felfel.features.alarm.alarmList.AlarmsListActivity
import com.rymo.felfel.features.alarm.DynamicThemeHandler
import com.rymo.felfel.features.alarm.ScheduledReceiver
import com.rymo.felfel.features.alarm.ToastPresenter
import com.rymo.felfel.features.alarm.alarmDetails.AlarmDetailsViewModel
import com.rymo.felfel.features.alarm.alarmList.AlarmListViewModel
import com.rymo.felfel.features.contacts.ContactsViewModel
import com.rymo.felfel.features.permissions.GeneratePermissionViewModel
import com.rymo.felfel.features.reports.ReportsViewModel
import com.rymo.felfel.features.splash.SplashViewModel
import com.rymo.felfel.repo.ExcelRepoImpl
import com.rymo.felfel.repo.ExcelRepository
import com.rymo.felfel.services.http.createApiServiceInstance
import com.rymo.felfel.stores.SharedRxDataStoreFactory
import com.rymo.felfel.util.Optional
import com.rymo.felfel.wakelock.WakeLockManager
import com.rymo.felfel.wakelock.Wakelocks
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.ArrayList
import java.util.Calendar
import org.koin.core.Koin
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun Scope.logger(tag: String): Logger {
    return get<LoggerFactory>().createLogger(tag)
}

fun Koin.logger(tag: String): Logger {
    return get<LoggerFactory>().createLogger(tag)
}

fun startKoin(context: Context): Koin {
    // The following line triggers the initialization of ACRA

    val module = module {
        single<DynamicThemeHandler> { DynamicThemeHandler(get()) }
        single<BugReporter> { BugReporter(logger("BugReporter"), context) }
        factory<Context> { context }
        factory(named("dateFormatOverride")) { "none" }
        factory<Single<Boolean>>(named("dateFormat")) {
            Single.fromCallable {
                get<String>(named("dateFormatOverride")).let { if (it == "none") null else it.toBoolean() }
                    ?: android.text.format.DateFormat.is24HourFormat(context)
            }
        }

        single<Prefs> {
            val factory = SharedRxDataStoreFactory.create(get(), logger("preferences"))
            Prefs.create(get(named("dateFormat")), factory)
        }

        single<Store> {
            Store(
                alarmsSubject = BehaviorSubject.createDefault(ArrayList()),
                next = BehaviorSubject.createDefault<Optional<Store.Next>>(Optional.absent()),
                sets = PublishSubject.create(),
                events = PublishSubject.create()
            )
        }
        factory { get<Context>().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
        single<AlarmSetter> { AlarmSetter.AlarmSetterImpl(logger("AlarmSetter"), get(), get()) }
        factory { Calendars { Calendar.getInstance() } }
        single<AlarmsScheduler> {
            AlarmsScheduler(get(), logger("AlarmsScheduler"), get(), get(), get())
        }
        factory<IAlarmsScheduler> { get<AlarmsScheduler>() }
        single<AlarmCore.IStateNotifier> { AlarmStateNotifier(get()) }
        single<ContainerFactory> { PersistingContainerFactory(get(), get()) }
        factory { get<Context>().contentResolver }
        single<DatabaseQuery> { RetryingDatabaseQuery(get(), get(), logger("DatabaseQuery")) }
        single<AlarmCoreFactory> {
            AlarmCoreFactory(logger("AlarmCore"), get(), get(), get(), get(), get())
        }
        single<Alarms> { Alarms(get(), get(), get(), get(), logger("Alarms")) }
        factory<IAlarmsManager> { get<Alarms>() }
        single {
            ScheduledReceiver(
                get(), get(), get(), get()
            )
        }
        single { ToastPresenter(get(), get()) }
        single { AlertServicePusher(get(), get(), get(), logger("AlertServicePusher")) }
        single { BackgroundNotifications(get(), get(), get(), get(), get()) }
        factory<Wakelocks> { get<WakeLockManager>() }
        factory<Scheduler> { AndroidSchedulers.mainThread() }
        single<WakeLockManager> { WakeLockManager(logger("WakeLockManager"), get()) }
        factory { get<Context>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
        factory { get<Context>().getSystemService(Context.POWER_SERVICE) as PowerManager }
        factory { get<Context>().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }
        factory { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
        factory { get<Context>().getSystemService(Context.AUDIO_SERVICE) as AudioManager }
        factory { get<Context>().resources }

        factory(named("volumePreferenceDemo")) {
            KlaxonPlugin(
                log = logger("VolumePreference"),
                playerFactory = { PlayerWrapper(get(), get(), logger("VolumePreference")) },
                prealarmVolume = get<Prefs>().preAlarmVolume.observe(),
                fadeInTimeInMillis = Observable.just(100),
                inCall = Observable.just(false),
                scheduler = get()
            )
        }

        single { createDataBaseInstance(context) }
        single { createApiServiceInstance() }

        factory {
            ExcelRepoImpl(get())
        }

        viewModel { GeneratePermissionViewModel() }
        viewModel { AlarmListViewModel(get()) }
        viewModel { SplashViewModel(get()) }
        viewModel { AlarmDetailsViewModel(get()) }
        viewModel { ContactsViewModel(get(), get()) }
        viewModel { ReportsViewModel(get(), get()) }

    }

    return startKoin {
        modules(module)
        modules(AlarmsListActivity.uiStoreModule)
        modules(loggerModule())
    }
        .koin
}

fun overrideIs24hoursFormatOverride(is24hours: Boolean) {
    loadKoinModules(
        module =
        module(override = true) { factory(named("dateFormatOverride")) { is24hours.toString() } })
}
