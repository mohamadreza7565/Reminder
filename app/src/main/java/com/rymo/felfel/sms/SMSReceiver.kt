package com.rymo.felfel.receiver.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage

abstract class SMSReceiver : BroadcastReceiver() {

    protected var context: Context? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        val smsMap: Map<String, String> = getMessage(intent)
        for (phone in smsMap.keys) {
            val msg = smsMap[phone]
            onMessageReceived(intent, phone, msg)
        }
    }

   private fun getMessage(intent: Intent?): Map<String, String> {
        val map = HashMap<String, String>()
        val bundle = intent?.extras ?: return map
        val pdus = bundle["pdus"] as Array<Any>? ?: return map
        val messages = arrayOfNulls<SmsMessage>(pdus.size)
        for (i in pdus.indices) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val format = bundle.getString("format")
                messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
            } else {
                messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
            }
            if (map.containsKey(messages[i]?.displayOriginatingAddress)) {
                var body = map[messages[i]?.displayOriginatingAddress]
                body += messages[i]?.displayMessageBody
                map[messages[i]!!.displayOriginatingAddress] = body!!
            } else {
                map[messages[i]!!.displayOriginatingAddress] =
                    messages[i]!!.displayMessageBody
            }
        }
        return map
    }

    protected abstract fun onMessageReceived(intent: Intent?, phone: String?, message: String?)


}