package com.rymo.felfel.wakelock

interface Wakelocks {
  fun acquireServiceLock()

  fun releaseServiceLock()
}
