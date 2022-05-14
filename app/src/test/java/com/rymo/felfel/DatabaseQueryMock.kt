package com.rymo.felfel

import com.rymo.felfel.model.AlarmStore
import com.rymo.felfel.model.ContainerFactory
import com.rymo.felfel.persistance.DatabaseQuery
import com.rymo.felfel.stores.modify

internal class DatabaseQueryMock {
  companion object {
    @JvmStatic
    fun createStub(list: MutableList<AlarmStore>): DatabaseQuery {
      return object : DatabaseQuery {
        override suspend fun query(): List<AlarmStore> {
          return list
        }
      }
    }

    @JvmStatic
    fun createWithFactory(factory: ContainerFactory): DatabaseQuery {
      return object : DatabaseQuery {
        override suspend fun query(): List<AlarmStore> {
          val container =
              factory.create().apply {
                modify { withId(100500).withIsEnabled(true).withLabel("hello") }
              }
          return listOf(container)
        }
      }
    }
  }
}
