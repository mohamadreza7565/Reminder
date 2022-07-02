package com.rymo.felfel.model

import android.content.Context
import com.rymo.felfel.R
import com.rymo.felfel.common.PersianCalendar
import com.rymo.felfel.common.toast
import timber.log.Timber
import java.text.DateFormatSymbols
import java.util.*

/*
 * Days of week code as a single int. 0x00: no day 0x01: Monday 0x02:
 * Tuesday 0x04: Wednesday 0x08: Thursday 0x10: Friday 0x20: Saturday 0x40:
 * Sunday
 */
data class DaysOfWeek(val coded: Int) {
    // Returns days of week encoded in an array of booleans.
    val booleanArray = BooleanArray(7) { index -> index.isSet() }
    val isRepeatSet = coded != 0

    fun toString(context: Context, showNever: Boolean): String {
        return when {
            coded == 0 && showNever -> context.getText(R.string.never).toString()
            coded == 0 -> ""
            // every day
            coded == 0x7f -> return context.getText(R.string.every_day).toString()
            // count selected days
            else -> {
                val dayCount = (0..6).count { it.isSet() }
                // short or long form?
                val dayStrings =
                    when {
                        dayCount > 1 -> DateFormatSymbols().shortWeekdays
                        else -> DateFormatSymbols().weekdays
                    }

                (0..6)
                    .filter { it.isSet() }
                    .map { dayIndex -> DAY_MAP[dayIndex] }
                    .map { calDay -> dayStrings[calDay] }
                    .joinToString(context.getText(R.string.day_concat))
            }
        }
    }

    private fun Int.isSet(): Boolean {
        return coded and (1 shl this) > 0
    }

    /** returns number of days from today until next alarm */
    fun getNextAlarm(today: Calendar): Int {
        val todayIndex = ((today.get(Calendar.DAY_OF_WEEK) + 5) % 7).getIranianSet()
        Timber.e("day index -> $todayIndex")
        return (0..6).firstOrNull { dayCount ->
            val day = (todayIndex + dayCount) % 7
            day.isSet()
        }
            ?: -1
    }

    override fun toString(): String {
        return (if (0.isSet()) "ش" else "_") +
            (if (1.isSet()) 'ی' else '_') +
            (if (2.isSet()) 'د' else '_') +
            (if (3.isSet()) 'س' else '_') +
            (if (4.isSet()) 'چ' else '_') +
            (if (5.isSet()) 'پ' else '_') +
            if (6.isSet()) 'ج' else '_'
    }



    companion object {
        private val DAY_MAP =
            intArrayOf(
                Calendar.SATURDAY,
                Calendar.SUNDAY,
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY,
            )
    }


}

fun Int.getIranianSet(): Int {
    return when(this){
        0 -> 2
        1 -> 3
        2 -> 4
        3 -> 5
        4 -> 6
        5 -> 0
        6 -> 1
        else -> -1
    }
}
