/*
 * Copyright (C) 2007 The Android Open Source Project
 * Copyright (C) 2012 Yuriy Kulikov yuriy.kulikov.87@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rymo.felfel.background

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.rymo.felfel.*
import com.rymo.felfel.common.PersianCalendar
import com.rymo.felfel.common.SimUtil
import com.rymo.felfel.common.convertArabic
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.database.createDataBaseInstance
import com.rymo.felfel.features.main.MainActivity
import com.rymo.felfel.interfaces.Intents
import com.rymo.felfel.interfaces.PresentationToModelIntents
import com.rymo.felfel.logger.Logger
import com.rymo.felfel.model.ExportSmsMessage
import com.rymo.felfel.model.SmsMessageModel
import com.rymo.felfel.model.SmsMessageSendTime
import com.rymo.felfel.repo.ExcelRepoImpl
import timber.log.Timber
import java.util.*

/**
 * Glue class: connects AlarmAlert IntentReceiver to AlarmAlert activity. Passes through Alarm ID.
 */
class NotificationsPlugin(
    private val logger: Logger,
    private val mContext: Context,
    private val nm: NotificationManager,
    private val enclosingService: EnclosingService
) {
    fun show(alarm: PluginAlarmData, index: Int, startForeground: Boolean) {
        // Trigger a notification that, when clicked, will show the alarm
        // alert dialog. No need to check for fullscreen since this will always
        // be launched from a user action.


        val pCal = PersianCalendar.getInstance()
        val smsTime = pCal.time.convertArabic()
        val smsDate = pCal.iranianDate.convertArabic()

        val appDatabase = createDataBaseInstance(mContext)
        val contactDao = appDatabase.contactDao()
        val smsMessageDao = appDatabase.smsMessageDao()
        val alarmContacts = contactDao.getContactAlarm(alarm.id.toLong())

        smsMessageDao.deleteExportMessage()

        alarmContacts.forEach {
            val contact = contactDao.getContact(it.contactId)
            contact?.let {
                smsMessageDao.insertExportMessage(
                    ExportSmsMessage(
                        contactName = it.nameAndFamily,
                        phoneNumber = it.phone,
                        date = smsDate,
                        time = smsTime,
                        textSms = alarm.label,
                        replayText = "",
                        companyName = contact.companyName
                    )
                )
            }
        }

        Timber.e("Export data -> ${smsMessageDao.getAllExportMessages().size}")

        export(date = smsDate, time = smsTime)


        smsMessageDao.insertSmsMessageTime(SmsMessageSendTime(date = smsDate, time = smsTime, textSms = alarm.label))
        Timber.e("Delay -> ${alarm.delay}")
        Thread {
            try {
                alarmContacts.forEach {
                    val contact = contactDao.getContact(it.contactId)
                    if (contact != null) {
                        val messageToSend = alarm.label
                        val number = contact.phone
                        SimUtil.sendSMS(alarm.simId, number, messageToSend)
                        Timber.e("Send To -> ${contact.phone}")
                        val smsMessageModel = SmsMessageModel(
                            contactName = contact.nameAndFamily,
                            phoneNumber = contact.phone,
                            time = smsTime,
                            date = smsDate,
                            replayText = "",
                            textSms = messageToSend,
                            companyName = contact.companyName
                        )
                        smsMessageDao.insertMessage(smsMessageModel)
                    }
                    Thread.sleep(alarm.delay)
                }
            } catch (e: Exception) {
            }
        }.start()




        Setting.lastMessageDate = Calendar.getInstance().time.time

        val notify = Intent(mContext, MainActivity::class.java)
        notify.putExtra(Intents.EXTRA_ID, alarm.id)
        val pendingNotify =
            PendingIntent.getActivity(mContext, alarm.id, notify, pendingIntentUpdateCurrentFlag())
        val pendingSnooze =
            PresentationToModelIntents.createPendingIntent(
                mContext, PresentationToModelIntents.ACTION_REQUEST_SNOOZE, alarm.id
            )
        val pendingDismiss =
            PresentationToModelIntents.createPendingIntent(
                mContext, PresentationToModelIntents.ACTION_REQUEST_DISMISS, alarm.id
            )

        val notification =
            mContext.notificationBuilder(CHANNEL_ID_HIGH_PRIO) {
                setContentTitle(alarm.label)
                setContentText("پیام های امروز ارسال شدند")
                setSmallIcon(R.drawable.stat_notify_alarm)
                priority = NotificationCompat.PRIORITY_HIGH
                setCategory(NotificationCompat.CATEGORY_ALARM)
                // setFullScreenIntent to show the user AlarmAlert dialog at the same time
                // when the Notification Bar was created.
                setFullScreenIntent(pendingNotify, true)
                // setContentIntent to show the user AlarmAlert dialog
                // when he will click on the Notification Bar.
                setContentIntent(pendingNotify)
                setOngoing(true)
                setDefaults(Notification.DEFAULT_LIGHTS)
            }

        pendingDismiss.send()

//        if (startForeground && isOreo()) {
//            logger.debug { "startForeground() for ${alarm.id}" }
//            enclosingService.startForeground(index + OFFSET, notification)
//        } else {
//            logger.debug { "nm.notify() for ${alarm.id}" }
//            nm.notify(index + OFFSET, notification)
//        }
    }

    fun cancel(index: Int) {
        nm.cancel(index + OFFSET)
    }

    private fun getString(id: Int) = mContext.getString(id)

    companion object {
        private const val OFFSET = 100000
    }

    private fun export(date: String, time: String) {
        ExcelRepoImpl().exportReports(date = date, time = time)
    }

}
