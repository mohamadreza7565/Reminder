package com.rymo.felfel

import android.database.Cursor
import com.rymo.felfel.model.AlarmStore
import com.rymo.felfel.model.Calendars
import com.rymo.felfel.model.ContainerFactory
import com.rymo.felfel.persistance.PersistingContainerFactory
import com.rymo.felfel.stores.InMemoryRxDataStoreFactory.Companion.inMemoryRxDataStore

/** Created by Yuriy on 25.06.2017. */
class TestContainerFactory(private val calendars: Calendars) : ContainerFactory {
  private var idCounter: Int = 0
  val createdRecords = mutableListOf<AlarmStore>()

  override fun create(): AlarmStore {
    val inMemory =
        inMemoryRxDataStore(
            PersistingContainerFactory.create(
                calendars = calendars, idMapper = { _ -> idCounter++ }))
    return object : AlarmStore {
          override var value = inMemory.value
          override fun observe() = inMemory.observe()
          override fun delete() {
            createdRecords.removeIf { it.value.id == value.id }
          }
        }
        .also { createdRecords.add(it) }
  }

  override fun create(cursor: Cursor): AlarmStore {
    throw UnsupportedOperationException()
  }
}
