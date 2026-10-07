package com.festiverse.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.festiverse.app.data.FestivalRepository;
import com.festiverse.app.model.Festival;
import com.festiverse.app.util.NotificationHelper;

/** Fires on the festival morning and posts the reminder notification. */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String customTitle = intent.getStringExtra("custom_title");
        if (customTitle != null) {
            NotificationHelper.createChannel(context);
            Intent open = new Intent(context, MainActivity.class);
            open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            int cflags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= 23) cflags |= PendingIntent.FLAG_IMMUTABLE;
            PendingIntent cpi = PendingIntent.getActivity(
                    context, customTitle.hashCode(), open, cflags);
            Notification.Builder cb;
            if (Build.VERSION.SDK_INT >= 26) {
                cb = new Notification.Builder(context, NotificationHelper.CHANNEL_ID);
            } else {
                cb = new Notification.Builder(context);
            }
            cb.setSmallIcon(android.R.drawable.ic_popup_reminder)
                    .setContentTitle(customTitle + " is today!")
                    .setContentText("Reminder from your FestiVerse calendar.")
                    .setAutoCancel(true)
                    .setContentIntent(cpi);
            NotificationManager cnm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (cnm != null) cnm.notify(("ev" + customTitle).hashCode(), cb.build());

            // Re-arm for next Nepali year (bookmarks recur annually).
            String entry = intent.getStringExtra("custom_entry");
            if (entry != null) NotificationHelper.scheduleCustom(context, entry);
            return;
        }

        String name = intent.getStringExtra("festival_name");
        String nepali = intent.getStringExtra("festival_nepali");
        if (name == null) return;

        NotificationHelper.createChannel(context);

        // Tapping the notification opens the festival's detail page.
        Festival target = null;
        for (Festival f : FestivalRepository.get().getAllFestivals()) {
            if (f.getName().equals(name)) { target = f; break; }
        }
        Intent open = new Intent(context, DetailActivity.class);
        if (target != null) open.putExtra(DetailActivity.EXTRA_FESTIVAL, target);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent content = PendingIntent.getActivity(
                context, name.hashCode(), open, flags);

        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) {
            b = new Notification.Builder(context, NotificationHelper.CHANNEL_ID);
        } else {
            b = new Notification.Builder(context);
        }
        b.setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(name + " is today!")
                .setContentText((nepali != null ? nepali + " — " : "")
                        + "Shubhakamana! Tap to read about this festival.")
                .setAutoCancel(true)
                .setContentIntent(content);

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(name.hashCode(), b.build());
    }
}
