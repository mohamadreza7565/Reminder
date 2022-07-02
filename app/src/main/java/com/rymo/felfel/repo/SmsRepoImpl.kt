package com.rymo.felfel.repo

import com.rymo.felfel.services.http.ApiService
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SmsRepoImpl(private val apiService: ApiService) : SmsRepo {

    override fun sendSms(mobiles: MutableList<String>, text: String): Single<MutableList<Long>> {

        var mobileNumbers = ""
        mobiles.forEach {
            mobileNumbers += "$it,"
        }

        return apiService.sendSms(mobileNumbers.substring(0, mobileNumbers.length - 1), text)

    }

}
