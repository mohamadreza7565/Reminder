package com.rymo.felfel.persistance

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.rymo.felfel.model.AlarmStore
import com.rymo.felfel.model.AlarmValue
import com.rymo.felfel.model.Alarmtone
import com.rymo.felfel.model.Calendars
import com.rymo.felfel.model.ContainerFactory
import com.rymo.felfel.model.DaysOfWeek
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.Calendar

/**
 * Active record container for all alarm data.
 *
 * @author Yuriy
 */
class PersistingContainerFactory(private val calendars: Calendars, private val mContext: Context) :
    ContainerFactory {
    private val subscriptions = mutableMapOf<Int, Disposable>()

    private fun createStore(initial: AlarmValue): AlarmStore {
        class AlarmStoreIR : AlarmStore {
            private val subject = BehaviorSubject.createDefault(initial)
            override var value: AlarmValue
                get() = requireNotNull(subject.value)
                set(value) {
                    val values: ContentValues = value.createContentValues()
                    val uriWithAppendedId =
                        ContentUris.withAppendedId(Columns.contentUri(), value.id.toLong())
                    mContext.contentResolver.update(uriWithAppendedId, values, null, null)
                    subject.onNext(value)
                }

            override fun observe(): Observable<AlarmValue> = subject.hide()
            override fun delete() {
                subscriptions.remove(value.id)?.dispose()
                val uri = ContentUris.withAppendedId(Columns.contentUri(), value.id.toLong())
                mContext.contentResolver.delete(uri, "", null)
            }
        }

        return AlarmStoreIR()
    }

    override fun create(cursor: Cursor): AlarmStore {
        return createStore(fromCursor(cursor, calendars))
    }

    override fun create(): AlarmStore {
        return createStore(
            create(
                calendars = calendars,
                idMapper = { container ->
                    val inserted: Uri? =
                        mContext.contentResolver.insert(
                            Columns.contentUri(), container.createContentValues()
                        )
                    ContentUris.parseId(requireNotNull(inserted)).toInt()
                })
        )
            .also { container ->
                // persist created container
                container.value = container.value
            }
    }

    private fun fromCursor(c: Cursor, calendars: Calendars): AlarmValue {
        val enabled = c.getInt(Columns.ALARM_ENABLED_INDEX) == 1
        return AlarmValue(
            id = c.getInt(Columns.ALARM_ID_INDEX),
            isEnabled = enabled,
            hour = c.getInt(Columns.ALARM_HOUR_INDEX),
            minutes = c.getInt(Columns.ALARM_MINUTES_INDEX),
            daysOfWeek = DaysOfWeek(c.getInt(Columns.ALARM_DAYS_OF_WEEK_INDEX)),
            isVibrate = c.getInt(Columns.ALARM_VIBRATE_INDEX) == 1,
            isPrealarm = c.getInt(Columns.ALARM_PREALARM_INDEX) == 1,
            label = c.getString(Columns.ALARM_MESSAGE_INDEX) ?: "",
            alarmtone = Alarmtone.fromString(c.getString(Columns.ALARM_ALERT_INDEX)),
            // it seems that there are still users with older databases which do not have the
            // ALARM_STATE_INDEX column
            state = c.getString(Columns.ALARM_STATE_INDEX)
                ?: (if (enabled) "NormalSetState" else "DisabledState"),
            nextTime = calendars.now().apply { timeInMillis = c.getLong(Columns.ALARM_TIME_INDEX) },
            delay = c.getLong(Columns.ALARM_DELAY_INDEX),
            simId = c.getInt(Columns.ALARM_SIM_ID_INDEX),
        )
    }

    companion object {
        @JvmStatic
        fun create(calendars: Calendars, idMapper: (AlarmValue) -> Int): AlarmValue {
            val now = calendars.now()

            val defaultActiveRecord =
                AlarmValue(
                    id = -1,
                    isEnabled = false,
                    hour = now.get(Calendar.HOUR_OF_DAY),
                    minutes = now.get(Calendar.MINUTE),
                    daysOfWeek = DaysOfWeek(0),
                    isVibrate = true,
                    isPrealarm = false,
                    label = "",
                    alarmtone = Alarmtone.Default,
                    state = "",
                    nextTime = now,
                    delay = 0,
                    simId = 0
                )

            // generate a new id
            val id = idMapper(defaultActiveRecord)
            return defaultActiveRecord.copy(id = id)
        }
    }

    private fun AlarmValue.createContentValues(): ContentValues {
        return ContentValues(12).apply {
            // id
            put(Columns.ENABLED, isEnabled)
            put(Columns.HOUR, hour)
            put(Columns.MINUTES, minutes)
            put(Columns.DAYS_OF_WEEK, daysOfWeek.coded)
            put(Columns.VIBRATE, isVibrate)
            put(Columns.MESSAGE, label)
            put(Columns.SIM_ID, simId)
            put(Columns.DELAY, delay)
            put(Columns.ALERT, alarmtone.persistedString)
            put(Columns.PREALARM, isPrealarm)
            put(Columns.ALARM_TIME, nextTime.timeInMillis)
            put(Columns.STATE, state)
        }
    }
}
