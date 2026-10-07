package com.festiverse.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.festiverse.app.util.NotificationHelper;

/** Restores all scheduled festival reminders after the device reboots. */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            NotificationHelper.rescheduleAll(context);
            NotificationHelper.rescheduleAllCustom(context);
        }
    }
}
