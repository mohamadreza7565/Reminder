package com.rymo.felfel.model

import com.rymo.felfel.configuration.Prefs
import com.rymo.felfel.configuration.Store
import com.rymo.felfel.logger.Logger

/** Created by Yuriy on 09.08.2017. */
class AlarmCoreFactory(
    private val logger: Logger,
    private val alarmsScheduler: IAlarmsScheduler,
    private val broadcaster: AlarmCore.IStateNotifier,
    private val prefs: Prefs,
    private val store: Store,
    private val calendars: Calendars
) {
  fun create(container: AlarmStore): AlarmCore {
    return AlarmCore(container, logger, alarmsScheduler, broadcaster, prefs, store, calendars)
  }
}
