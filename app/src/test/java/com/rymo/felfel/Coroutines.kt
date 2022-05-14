package com.rymo.felfel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain

fun setMainUnconfined() {
  Dispatchers.setMain(Dispatchers.Unconfined)
}
