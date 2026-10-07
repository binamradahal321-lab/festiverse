package com.festiverse.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat ISO =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat PRETTY =
            new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);

    public static Date parse(String iso) {
        try { return ISO.parse(iso); } catch (ParseException e) { return new Date(); }
    }

    public static String pretty(String iso) {
        return PRETTY.format(parse(iso));
    }

    private static Calendar startOfDay(String iso) {
        Calendar c = Calendar.getInstance();
        c.setTime(parse(iso));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /** Millis until the festival date this year (negative if passed). */
    public static long millisUntil(String iso) {
        return startOfDay(iso).getTimeInMillis() - System.currentTimeMillis();
    }

    public static boolean isPast(String iso) {
        Calendar end = startOfDay(iso);
        end.add(Calendar.DAY_OF_YEAR, 1);
        return end.getTimeInMillis() <= System.currentTimeMillis();
    }

    public static boolean isToday(String iso) {
        long ms = millisUntil(iso);
        return ms <= 0 && !isPast(iso);
    }

    /**
     * Millis until the next occurrence, following the NEPALI (BS) calendar:
     * once the festival's BS date has passed, it rolls to the same BS
     * month/day in the NEXT BS year (not simply AD date + 1 year).
     */
    public static long millisUntilNext(String iso) {
        BsDate fest = BsDate.fromIso(iso);
        BsDate today = BsDate.today();

        int targetYear;
        if (fest.month > today.month
                || (fest.month == today.month && fest.day >= today.day)) {
            targetYear = today.year;     // still ahead (or today) this BS year
        } else {
            targetYear = today.year + 1; // passed in this BS year -> next BS year
        }
        int day = Math.min(fest.day, BsDate.daysInMonth(targetYear, fest.month));
        Calendar target = BsDate.toAd(targetYear, fest.month, day);
        return Math.max(target.getTimeInMillis() - System.currentTimeMillis(), 0);
    }

    /**
     * Absolute epoch millis (midnight) of the festival's NEXT occurrence in
     * the Nepali calendar. Stable value — safe to precompute and sort by.
     */
    public static long nextOccurrenceEpoch(String iso) {
        BsDate fest = BsDate.fromIso(iso);
        BsDate today = BsDate.today();
        int targetYear;
        if (fest.month > today.month
                || (fest.month == today.month && fest.day >= today.day)) {
            targetYear = today.year;
        } else {
            targetYear = today.year + 1;
        }
        int day = Math.min(fest.day, BsDate.daysInMonth(targetYear, fest.month));
        return BsDate.toAd(targetYear, fest.month, day).getTimeInMillis();
    }

    /** The BS year of the festival's next occurrence (for labels). */
    public static int nextOccurrenceBsYear(String iso) {
        BsDate fest = BsDate.fromIso(iso);
        BsDate today = BsDate.today();
        if (fest.month > today.month
                || (fest.month == today.month && fest.day >= today.day)) {
            return today.year;
        }
        return today.year + 1;
    }

    /**
     * Passed per the NEPALI calendar: true when the festival's BS
     * (month, day) has already gone by in the current BS year.
     */
    public static boolean isPastBs(String iso) {
        BsDate fest = BsDate.fromIso(iso);
        BsDate today = BsDate.today();
        return fest.month < today.month
                || (fest.month == today.month && fest.day < today.day);
    }

    /** Today per the NEPALI calendar (same BS month and day). */
    public static boolean isTodayBs(String iso) {
        BsDate fest = BsDate.fromIso(iso);
        BsDate today = BsDate.today();
        return fest.month == today.month && fest.day == today.day;
    }

    public static long daysUntil(String iso) {
        return millisUntil(iso) / (1000L * 60 * 60 * 24);
    }

    /** {days, hours, minutes, seconds} from a millis duration. */
    public static long[] breakdown(long millis) {
        if (millis < 0) millis = 0;
        long d = millis / 86400000L; millis -= d * 86400000L;
        long h = millis / 3600000L;  millis -= h * 3600000L;
        long m = millis / 60000L;    millis -= m * 60000L;
        long s = millis / 1000L;
        return new long[]{d, h, m, s};
    }
}
