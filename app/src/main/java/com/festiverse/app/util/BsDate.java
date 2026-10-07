package com.festiverse.app.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Bikram Sambat (BS) calendar engine.
 *
 * Month-length table generated and verified against the official BS calendar
 * (anchors checked: 2080/81/82/83 New Year dates). Covers BS 2070-2099.
 */
public class BsDate {

    public static final int START_YEAR = 2070;
    public static final int END_YEAR = 2099;

    /** AD date of BS 2070-01-01. */
    private static final int ANCHOR_AD_YEAR = 2013;
    private static final int ANCHOR_AD_MONTH = 4;   // 1-based
    private static final int ANCHOR_AD_DAY = 14;

    /** Days in each BS month, rows = years 2070..2099. */
    private static final int[][] DAYS = {
            {31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30},   // 2070
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},   // 2071
            {31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30},   // 2072
            {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31},   // 2073
            {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},   // 2074
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},   // 2075
            {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},   // 2076
            {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},   // 2077
            {31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30},   // 2078
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},   // 2079
            {31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30},   // 2080
            {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31},   // 2081
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},   // 2082
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30},   // 2083
            {31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30},   // 2084
            {31, 32, 31, 32, 30, 31, 30, 30, 29, 30, 30, 30},   // 2085
            {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},   // 2086
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 30, 30, 30},   // 2087
            {30, 31, 32, 32, 30, 31, 30, 30, 29, 30, 30, 30},   // 2088
            {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},   // 2089
            {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},   // 2090
            {31, 31, 32, 31, 31, 31, 30, 30, 29, 30, 30, 30},   // 2091
            {30, 31, 32, 32, 31, 30, 30, 30, 29, 30, 30, 30},   // 2092
            {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},   // 2093
            {31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30},   // 2094
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 30, 30, 30},   // 2095
            {30, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30},   // 2096
            {31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30},   // 2097
            {31, 31, 32, 31, 31, 31, 29, 30, 29, 30, 29, 31},   // 2098
            {31, 31, 32, 31, 31, 31, 30, 29, 29, 30, 30, 30}   // 2099
    };

    public static final String[] MONTHS_NP = {
            "वैशाख", "जेठ", "असार", "साउन", "भदौ", "असोज",
            "कात्तिक", "मंसिर", "पुस", "माघ", "फागुन", "चैत"
    };

    public static final String[] MONTHS_EN = {
            "Baisakh", "Jestha", "Asar", "Shrawan", "Bhadra", "Asoj",
            "Kartik", "Mangsir", "Poush", "Magh", "Falgun", "Chaitra"
    };

    public static final String[] WEEKDAYS_NP = {
            "आइत", "सोम", "मंगल", "बुध", "बिहि", "शुक्र", "शनि"
    };

    private static final char[] NP_DIGITS =
            {'०', '१', '२', '३', '४', '५', '६', '७', '८', '९'};

    public final int year;    // BS year
    public final int month;   // 1-12
    public final int day;     // 1-32

    public BsDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static int daysInMonth(int bsYear, int bsMonth) {
        if (bsYear < START_YEAR || bsYear > END_YEAR) return 30;
        return DAYS[bsYear - START_YEAR][bsMonth - 1];
    }

    private static Calendar anchorCal() {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.clear();
        c.set(ANCHOR_AD_YEAR, ANCHOR_AD_MONTH - 1, ANCHOR_AD_DAY, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /** Days from BS 2070-01-01 to the given BS date. */
    private static int offsetDays(int y, int m, int d) {
        int days = 0;
        for (int yy = START_YEAR; yy < y; yy++)
            for (int mm = 1; mm <= 12; mm++)
                days += DAYS[yy - START_YEAR][mm - 1];
        for (int mm = 1; mm < m; mm++)
            days += DAYS[y - START_YEAR][mm - 1];
        return days + (d - 1);
    }

    /** BS -> AD (midnight local). */
    public static Calendar toAd(int bsYear, int bsMonth, int bsDay) {
        Calendar c = anchorCal();
        c.add(Calendar.DAY_OF_YEAR, offsetDays(bsYear, bsMonth, bsDay));
        return c;
    }

    /** AD -> BS. */
    public static BsDate fromAd(Calendar ad) {
        Calendar a = (Calendar) ad.clone();
        a.set(Calendar.HOUR_OF_DAY, 0);
        a.set(Calendar.MINUTE, 0);
        a.set(Calendar.SECOND, 0);
        a.set(Calendar.MILLISECOND, 0);
        long diffMs = a.getTimeInMillis() - anchorCal().getTimeInMillis();
        int days = (int) Math.round(diffMs / 86400000.0);
        int y = START_YEAR, m = 1;
        while (true) {
            int dim = daysInMonth(y, m);
            if (days < dim) break;
            days -= dim;
            if (++m > 12) { m = 1; y++; if (y > END_YEAR) break; }
        }
        return new BsDate(y, m, days + 1);
    }

    public static BsDate today() {
        return fromAd(Calendar.getInstance());
    }

    public static BsDate fromIso(String iso) {
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtils.parse(iso));
        return fromAd(c);
    }

    /** Converts 2083 -> "२०८३". */
    public static String np(int number) {
        StringBuilder sb = new StringBuilder();
        for (char ch : String.valueOf(number).toCharArray()) {
            if (ch >= '0' && ch <= '9') sb.append(NP_DIGITS[ch - '0']);
            else sb.append(ch);
        }
        return sb.toString();
    }

    public String monthNp() { return MONTHS_NP[month - 1]; }
    public String monthEn() { return MONTHS_EN[month - 1]; }

    /** e.g. "असोज १६, २०८३" */
    public String prettyNp() {
        return monthNp() + " " + np(day) + ", " + np(year);
    }

    /** Ordinal comparison. */
    public int compareTo(BsDate o) {
        if (year != o.year) return year - o.year;
        if (month != o.month) return month - o.month;
        return day - o.day;
    }
}
