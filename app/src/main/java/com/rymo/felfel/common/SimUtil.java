package com.rymo.felfel.common;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import com.rymo.felfel.BuildConfig;
import com.rymo.felfel.configuration.AlarmApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SimUtil {

    public static void sendSMS(int simID, String number, String messageToSend) {
        SmsManager.getSmsManagerForSubscriptionId(simID).sendTextMessage(number, null, messageToSend, null, null);
    }

    public static ArrayList<SubscriptionInfo> getSimCount() {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(AlarmApplication.Companion.getInstance());
            @SuppressLint("MissingPermission") final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                    .getActiveSubscriptionInfoList();
        return new ArrayList<>(subscriptionInfoList);
    }
}