package com.rymo.felfel.services.http

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.rymo.felfel.configuration.AlarmApplication
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import timber.log.Timber
import java.util.concurrent.TimeUnit


interface ApiService {

    @FormUrlEncoded
    @POST("send_sms")
    fun sendSms(
        @Field("mobile_numbers") mobiles: String,
        @Field("text") text: String,
    ): Single<MutableList<Long>>


}


fun createApiServiceInstance(): ApiService {

    val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(ChuckerInterceptor(AlarmApplication.instance!!))
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(20, TimeUnit.SECONDS)
        .addInterceptor {
            val oldRequest = it.request()
            val newRequestBuilder = oldRequest.newBuilder()

            newRequestBuilder.addHeader("Accept", "application/json")

            newRequestBuilder.method(oldRequest.method(), oldRequest.body())

            return@addInterceptor it.proceed(newRequestBuilder.build())
        }.build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://sefaresh.sabziyaneh.com/api/v1/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    return retrofit.create(ApiService::class.java)

}
