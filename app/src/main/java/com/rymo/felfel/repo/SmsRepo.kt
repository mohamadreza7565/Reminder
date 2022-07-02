package com.rymo.felfel.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface SmsRepo {

    fun sendSms(mobiles: MutableList<String>, text: String): Single<MutableList<Long>>

}
