package com.rymo.felfel.background

import android.app.Notification
import com.rymo.felfel.BuildConfig
import com.rymo.felfel.interfaces.IAlarmsManager
import com.rymo.felfel.interfaces.Intents
import com.rymo.felfel.isOreo
import com.rymo.felfel.logger.Logger
import com.rymo.felfel.model.Alarmtone
import com.rymo.felfel.util.modify
import com.rymo.felfel.util.requireValue
import com.rymo.felfel.util.subscribeIn
import com.rymo.felfel.wakelock.Wakelocks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.util.Calendar

interface AlertPlugin {
    fun go(
        alarm: PluginAlarmData,
        prealarm: Boolean,
        targetVolume: Observable<TargetVolume>
    ): Disposable
}

data class PluginAlarmData(val id: Int, val alarmtone: Alarmtone, val label: String, val delay: Long, val simId: Int)

enum class TargetVolume {
    MUTED,
    FADED_IN,
    FADED_IN_FAST
}

sealed class Event {
    data class NullEvent(val actions: String = "null") : Event()
    data class AlarmEvent(val id: Int, val actions: String = Intents.ALARM_ALERT_ACTION) : Event()
    data class PrealarmEvent(val id: Int, val actions: String = Intents.ALARM_PREALARM_ACTION) :
        Event()

    data class DismissEvent(val id: Int, val actions: String = Intents.ALARM_DISMISS_ACTION) :
        Event()

    data class SnoozedEvent(
        val id: Int,
        val calendar: Calendar,
        val actions: String = Intents.ALARM_SNOOZE_ACTION
    ) : Event()

    data class ShowSkip(val id: Int, val actions: String = Intents.ALARM_SHOW_SKIP) : Event()
    data class HideSkip(val id: Int, val actions: String = Intents.ALARM_REMOVE_SKIP) : Event()
    data class CancelSnoozedEvent(val id: Int, val actions: String = Intents.ACTION_CANCEL_SNOOZE) :
        Event()

    data class Autosilenced(val id: Int, val actions: String = Intents.ACTION_SOUND_EXPIRED) :
        Event()

    data class MuteEvent(val actions: String = Intents.ACTION_MUTE) : Event()
    data class DemuteEvent(val actions: String = Intents.ACTION_DEMUTE) : Event()
}

interface EnclosingService {
    fun handleUnwantedEvent()
    fun stopSelf()
    fun startForeground(id: Int, notification: Notification)
}

/** Listens to all kinds of events, vibrates, shows notifications and so on. */
class AlertService(
    private val log: Logger,
    private val wakelocks: Wakelocks,
    private val alarms: IAlarmsManager,
    private val inCall: Observable<Boolean>,
    private val plugins: List<AlertPlugin>,
    private val notifications: NotificationsPlugin,
    private val enclosing: EnclosingService
) {
    private val wantedVolume: BehaviorSubject<TargetVolume> =
        BehaviorSubject.createDefault(TargetVolume.MUTED)

    private enum class Type {
        NORMAL,
        PREALARM
    }

    private data class CallState(val initial: Boolean, val inCall: Boolean)

    private val disposable: CompositeDisposable = CompositeDisposable()
    private var soundAlarmDisposable: CompositeDisposable = CompositeDisposable()

    private val activeAlarms = BehaviorSubject.createDefault(mapOf<Int, Type>())
    private var nowShowing = emptyList<Int>()

    init {
        wakelocks.acquireServiceLock()
        activeAlarms
            .distinctUntilChanged()
            .skipWhile { it.isEmpty() }
            .subscribeIn(disposable) { active ->
                if (active.isNotEmpty()) {
                    log.debug { "activeAlarms: $active" }
                    playSound(active)
                    showNotifications(active)
                } else {
                    log.debug { "no alarms anymore, stopSelf()" }
                    soundAlarmDisposable.dispose()
                    wantedVolume.onNext(TargetVolume.MUTED)
                    nowShowing
                        .filter { !isOreo() || it != 0 } // not the foreground notification
                        .forEach { notifications.cancel(it) }
                    enclosing.stopSelf()
                    disposable.dispose()
                }
            }
    }

    fun onDestroy() {
        log.debug { "onDestroy" }
        wakelocks.releaseServiceLock()
    }

    fun onStartCommand(event: Event): Boolean {
        log.debug { "onStartCommand $event" }

        return if (stateValid(event)) {
            when (event) {
                is Event.AlarmEvent -> soundAlarm(event.id, Type.NORMAL)
                is Event.PrealarmEvent -> soundAlarm(event.id, Type.PREALARM)
                is Event.MuteEvent -> wantedVolume.onNext(TargetVolume.MUTED)
                is Event.DemuteEvent -> wantedVolume.onNext(TargetVolume.FADED_IN_FAST)
                is Event.DismissEvent -> remove(event.id)
                is Event.SnoozedEvent -> remove(event.id)
                is Event.Autosilenced -> remove(event.id)
            }

            activeAlarms.requireValue().isNotEmpty()
        } else {
            enclosing.handleUnwantedEvent()
            false
        }
    }

    private fun stateValid(event: Event): Boolean {
        return when {
            activeAlarms.requireValue().isEmpty() -> {
                when (event) {
                    is Event.AlarmEvent -> true
                    is Event.PrealarmEvent -> true
                    else -> {
                        check(!BuildConfig.DEBUG) { "First event must be AlarmEvent or PrealarmEvent" }
                        false
                    }
                }
            }
            disposable.isDisposed -> {
                check(!BuildConfig.DEBUG) { "Already disposed!" }
                false
            }
            else -> true
        }
    }

    private fun remove(id: Int) {
        activeAlarms.modify { minus(id) }
    }

    private fun soundAlarm(id: Int, type: Type) {
        activeAlarms.modify { plus(id to type) }
    }

    private fun showNotifications(active: Map<Int, Type>) {
        require(active.isNotEmpty())
        val toShow =
            active
                .mapNotNull { (id, _) -> alarms.getAlarm(id) }
                .map { alarm ->
                    val alarmtone = alarm.alarmtone
                    val label = alarm.labelOrDefault
                    val delay = alarm.delay
                    val simId = alarm.simId
                    PluginAlarmData(alarm.id, alarmtone, label,delay, simId)
                }

        log.debug { "Show notifications: $toShow" }

        // Send the notification using the alarm id to easily identify the
        // correct notification.
        toShow.forEachIndexed { index, alarmData ->
            val startForeground = nowShowing.isEmpty() && index == 0
            log.debug { "notifications.show(${alarmData}, $index, $startForeground)" }
            notifications.show(alarmData, index, startForeground)
        }

        // cancel what we don't need anymore
        nowShowing.drop(toShow.size).forEach { notifications.cancel(it) }

        nowShowing = toShow.mapIndexed { index, _ -> index }
    }

    private fun playSound(active: Map<Int, Type>) {
        require(active.isNotEmpty())
        val (id, type) = active.entries.last()
        play(id, type)
    }

    private fun play(id: Int, type: Type) {
        // new alarm - dispose all current signals
        soundAlarmDisposable.dispose()

        wantedVolume.onNext(TargetVolume.FADED_IN)

        val targetVolumeAccountingForInCallState: Observable<TargetVolume> =
            Observable.combineLatest<TargetVolume, CallState, TargetVolume>(
                wantedVolume,
                inCall.zipWithIndex { callActive, index -> CallState(index == 0, callActive) },
                BiFunction { volume, callState ->
                    when {
                        !callState.initial && callState.inCall -> TargetVolume.MUTED
                        !callState.initial && !callState.inCall -> TargetVolume.FADED_IN_FAST
                        else -> volume
                    }
                })

        val alarm = alarms.getAlarm(id)
        val alarmtone = alarm?.alarmtone ?: Alarmtone.Default
        val label = alarm?.labelOrDefault ?: ""
        val delay = alarm?.delay ?: 0
        val simId = alarm?.simId ?: 0
        val pluginDisposables =
            plugins.map {
                it.go(
                    PluginAlarmData(id, alarmtone, label,delay, simId),
                    prealarm = type == Type.PREALARM,
                    targetVolume = targetVolumeAccountingForInCallState
                )
            }
        soundAlarmDisposable = CompositeDisposable(pluginDisposables)
    }

    private fun <U : Any, D : Any> Observable<U>.zipWithIndex(
        function: (U, Int) -> D
    ): Observable<D> {
        return zipWith(generateSequence(0) { it + 1 }.asIterable()) { next, index ->
            function.invoke(next, index)
        }
    }
}
