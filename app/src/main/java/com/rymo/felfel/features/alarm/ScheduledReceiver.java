/*
 * Copyright (C) 2012 Yuriy Kulikov yuriy.kulikov.87@gmail.com
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
package com.rymo.felfel.features.alarm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.format.DateFormat;
import com.rymo.felfel.OreoKt;
import com.rymo.felfel.configuration.Prefs;
import com.rymo.felfel.configuration.Store;
import com.rymo.felfel.features.alarm.alarmList.AlarmsListActivity;
import com.rymo.felfel.interfaces.Intents;
import com.rymo.felfel.util.Optional;

import java.util.Calendar;

/**
 * @author Yuriy
 */
public class ScheduledReceiver {
  private static final String DM12 = "E h:mm aa";
  private static final String DM24 = "E kk:mm";
  private static final Intent FAKE_INTENT_JUST_TO_DISPLAY_IN_ICON =
      new Intent("FAKE_ACTION_JUST_TO_DISPLAY_AN_ICON");
  private Store store;
  private Context context;
  private Prefs prefs;
  private AlarmManager am;

  public ScheduledReceiver(Store store, final Context context, Prefs prefs, AlarmManager am) {
    this.store = store;
    this.context = context;
    this.prefs = prefs;
    this.am = am;
  }

  @SuppressLint("CheckResult")
  public void start() {
    store
        .next()
        .subscribe(
                nextOptional -> {
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // we use setAlarmClock for these anyway, so nothing to do here.
                  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    doForLollipop(context, nextOptional);
                  } else {
                    doForPreLollipop(context, nextOptional);
                  }
                });
  }

  private void doForPreLollipop(Context context, Optional<Store.Next> nextOptional) {
    if (nextOptional.isPresent()) {
      // Broadcast intent for the notification bar
      Intent alarmChanged = new Intent("android.intent.action.ALARM_CHANGED");
      alarmChanged.putExtra("alarmSet", true);
      context.sendBroadcast(alarmChanged);

      // Update systems settings, so that interested Apps (like
      // KeyGuard)
      // will react accordingly
      String format = prefs.is24HourFormat().blockingGet() ? DM24 : DM12;
      Calendar calendar = Calendar.getInstance();
      long milliseconds = nextOptional.get().nextNonPrealarmTime();
      calendar.setTimeInMillis(milliseconds);
      String timeString = (String) DateFormat.format(format, calendar);
      Settings.System.putString(
          context.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED, timeString);
    } else {
      // Broadcast intent for the notification bar
      Intent alarmChanged = new Intent("android.intent.action.ALARM_CHANGED");
      alarmChanged.putExtra("alarmSet", false);
      context.sendBroadcast(alarmChanged);
      // Update systems settings, so that interested Apps (like KeyGuard)
      // will react accordingly
      Settings.System.putString(
          context.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED, "");
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void doForLollipop(Context context, Optional<Store.Next> nextOptional) {
    if (nextOptional.isPresent()) {
      int id = nextOptional.get().alarm().getId();

      Intent showList = new Intent(context, AlarmsListActivity.class);
      showList.putExtra(Intents.EXTRA_ID, id);
      PendingIntent showIntent =
          PendingIntent.getActivity(context, id, showList, OreoKt.pendingIntentUpdateCurrentFlag());

      long milliseconds = nextOptional.get().nextNonPrealarmTime();
      am.setAlarmClock(
          new AlarmClockInfo(milliseconds, showIntent),
          PendingIntent.getBroadcast(
              context,
              hashCode(),
              FAKE_INTENT_JUST_TO_DISPLAY_IN_ICON,
              OreoKt.pendingIntentUpdateCurrentFlag()));
    } else {
      am.cancel(
          PendingIntent.getBroadcast(
              context,
              hashCode(),
              FAKE_INTENT_JUST_TO_DISPLAY_IN_ICON,
              OreoKt.pendingIntentUpdateCurrentFlag()));
    }
  }
}
