package com.rymo.felfel.persistance

import android.net.Uri
import android.provider.BaseColumns
import com.rymo.felfel.BuildConfig

// ////////////////////////////
// Column definitions
// ////////////////////////////
class Columns : BaseColumns {
    companion object {
        /** The content:// style URL for this table */
        private val CONTENT_URI: Uri by lazy {
            Uri.parse("content://" + BuildConfig.APPLICATION_ID + ".model/alarm")
        }

        @JvmStatic
        fun contentUri() = CONTENT_URI

        /**
         * Hour in 24-hour localtime 0 - 23.
         *
         * Type: INTEGER
         */
        const val HOUR = "hour"

        /**
         * Minutes in localtime 0 - 59
         *
         * Type: INTEGER
         */
        const val MINUTES = "minutes"

        /**
         * Days of week coded as integer
         *
         * Type: INTEGER
         */
        const val DAYS_OF_WEEK = "daysofweek"

        /**
         * AlarmCore time in UTC milliseconds from the epoch.
         *
         * Type: INTEGER
         */
        const val ALARM_TIME = "alarmtime"

        /**
         * True if alarm is active
         *
         * Type: BOOLEAN
         */
        const val ENABLED = "enabled"

        /**
         * True if alarm should vibrate
         *
         * Type: BOOLEAN
         */
        const val VIBRATE = "vibrate"

        /**
         * Message to show when alarm triggers Note: not currently used
         *
         * Type: STRING
         */
        const val MESSAGE = "message"

        /**
         * Delay to show when alarm triggers Note: not currently used
         *
         * Type: STRING
         */
        const val DELAY = "delay"

        /**
         * SimId to show when alarm triggers Note: not currently used
         *
         * Type: STRING
         */
        const val SIM_ID = "simId"

        /**
         * Audio alert to play when alarm triggers
         *
         * Type: STRING
         */
        const val ALERT = "alert"

        /**
         * Use prealarm
         *
         * Type: STRING
         */
        const val PREALARM = "prealarm"

        /**
         * State machine state
         *
         * Type: STRING
         */
        const val STATE = "state"

        /** The default sort order for this table */
        const val DEFAULT_SORT_ORDER = "$HOUR, $MINUTES ASC"

        @JvmField
        val ALARM_QUERY_COLUMNS =
            arrayOf(
                BaseColumns._ID,
                HOUR,
                MINUTES,
                DAYS_OF_WEEK,
                ALARM_TIME,
                ENABLED,
                VIBRATE,
                MESSAGE,
                ALERT,
                PREALARM,
                STATE,
                DELAY,
                SIM_ID

            )

        /**
         * These save calls to cursor.getColumnIndexOrThrow() THEY MUST BE KEPT IN SYNC WITH ABOVE QUERY
         * COLUMNS
         */
        const val ALARM_ID_INDEX = 0
        const val ALARM_HOUR_INDEX = 1
        const val ALARM_MINUTES_INDEX = 2
        const val ALARM_DAYS_OF_WEEK_INDEX = 3
        const val ALARM_TIME_INDEX = 4
        const val ALARM_ENABLED_INDEX = 5
        const val ALARM_VIBRATE_INDEX = 6
        const val ALARM_MESSAGE_INDEX = 7
        const val ALARM_ALERT_INDEX = 8
        const val ALARM_PREALARM_INDEX = 9
        const val ALARM_STATE_INDEX = 10
        const val ALARM_DELAY_INDEX = 11
        const val ALARM_SIM_ID_INDEX = 12
    }
}
