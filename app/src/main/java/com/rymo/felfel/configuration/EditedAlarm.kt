package com.rymo.felfel.configuration

import com.rymo.felfel.model.AlarmValue
import com.rymo.felfel.features.alarm.RowHolder
import com.rymo.felfel.util.Optional

/** Created by Yuriy on 09.08.2017. */
data class EditedAlarm(
    val isNew: Boolean = false,
    val id: Int = -1,
    val value: Optional<AlarmValue> = Optional.absent(),
    val holder: Optional<RowHolder> = Optional.absent()
) {
  fun id() = id
  val isEdited: Boolean = value.isPresent()
}
