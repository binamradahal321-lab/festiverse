package com.festiverse.app.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.festiverse.app.NotificationReceiver;
import com.festiverse.app.model.Festival;

import java.util.Calendar;

/**
 * Schedules a reminder notification on the morning (7:00 AM) of each
 * festival the user opted into. If this year's date has passed, the
 * reminder is scheduled for next year's occurrence.
 */
public class NotificationHelper {

    public static final String CHANNEL_ID = "festival_reminders";

    public static void createChannel(Context c) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Festival Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Reminds you when a festival arrives");
            NotificationManager nm = c.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    /**
     * 7:00 AM of the festival's NEXT occurrence, following the Nepali
     * calendar (rolls to the same BS date next BS year once passed).
     */
    public static long triggerTimeFor(Festival f) {
        BsDate bs = BsDate.fromIso(f.getDate());
        return triggerTimeForBs(bs.month, bs.day);
    }

    private static PendingIntent pending(Context c, Festival f) {
        Intent i = new Intent(c, NotificationReceiver.class);
        i.putExtra("festival_name", f.getName());
        i.putExtra("festival_nepali", f.getNepaliName());
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(c, f.getName().hashCode(), i, flags);
    }

    public static void schedule(Context c, Festival f) {
        createChannel(c);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        long t = triggerTimeFor(f);
        if (Build.VERSION.SDK_INT >= 23) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pending(c, f));
        } else {
            am.set(AlarmManager.RTC_WAKEUP, t, pending(c, f));
        }
        Prefs.setNotify(c, f.getName(), true);
    }

    public static void cancel(Context c, Festival f) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(pending(c, f));
        Prefs.setNotify(c, f.getName(), false);
    }

    // ---------- Custom calendar bookmarks (events & birthdays) ----------

    /** Next 7:00 AM occurrence of a BS month/day (this BS year or next). */
    public static long triggerTimeForBs(int bsMonth, int bsDay) {
        BsDate today = BsDate.today();
        int year = today.year;
        if (bsMonth < today.month
                || (bsMonth == today.month && bsDay < today.day)) {
            year++;
        }
        int day = Math.min(bsDay, BsDate.daysInMonth(year, bsMonth));
        java.util.Calendar cal = BsDate.toAd(year, bsMonth, day);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 7);
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            year++;
            day = Math.min(bsDay, BsDate.daysInMonth(year, bsMonth));
            cal = BsDate.toAd(year, bsMonth, day);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 7);
        }
        return cal.getTimeInMillis();
    }

    private static PendingIntent customPending(Context c, String entry, String title) {
        Intent i = new Intent(c, com.festiverse.app.NotificationReceiver.class);
        i.putExtra("custom_entry", entry);
        i.putExtra("custom_title", title);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(c, ("ev" + entry).hashCode(), i, flags);
    }

    /** Schedules a reminder for a saved bookmark entry "m|d|type|title". */
    public static void scheduleCustom(Context c, String entry) {
        String[] parts = entry.split("\\|", 4);
        if (parts.length < 4) return;
        createChannel(c);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        long t = triggerTimeForBs(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        PendingIntent pi = customPending(c, entry, parts[3]);
        if (Build.VERSION.SDK_INT >= 23) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, t, pi);
        }
    }

    public static void cancelCustom(Context c, String entry) {
        String[] parts = entry.split("\\|", 4);
        if (parts.length < 4) return;
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(customPending(c, entry, parts[3]));
    }

    public static void rescheduleAllCustom(Context c) {
        for (String entry : Prefs.getEvents(c)) scheduleCustom(c, entry);
    }

    /** Re-schedules every saved reminder (used after device reboot). */
    public static void rescheduleAll(Context c) {
        java.util.Set<String> names = Prefs.getNotifySet(c);
        for (Festival f : com.festiverse.app.data.FestivalRepository.get().getAllFestivals()) {
            if (names.contains(f.getName())) schedule(c, f);
        }
    }
}
