package com.tevinjeffrey.njitct.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tevinjeffrey.njitct.NjitCTApp;
import com.tevinjeffrey.njitct.services.Alarm;

import timber.log.Timber;

//This receiver is called by the android system when the device wakes.
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Timber.d("BootReceiver: ", "Caught android.intent.action.BOOT_COMPLETED");

            //Set alarm when the device boots.
            NjitCTApp.getObjectGraph(context).get(Alarm.class).setAlarm();
        }
    }
}
