package com.festiverse.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Local storage for the demo login system and theme choice.
 * NOTE: on-device demo auth only — replace with a real backend or
 * Firebase Auth before shipping to production.
 */
public class Prefs {

    private static final String FILE = "festiverse_prefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASS_HASH = "user_pass_hash";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_DARK_MODE = "dark_mode";

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean accountExists(Context c) {
        return sp(c).contains(KEY_EMAIL);
    }

    public static void createAccount(Context c, String name, String email, String password) {
        sp(c).edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email.trim().toLowerCase())
                .putString(KEY_PASS_HASH, hash(password))
                .apply();
    }

    public static boolean checkLogin(Context c, String email, String password) {
        String savedEmail = sp(c).getString(KEY_EMAIL, null);
        String savedHash = sp(c).getString(KEY_PASS_HASH, null);
        if (savedEmail == null || savedHash == null) return false;
        return savedEmail.equals(email.trim().toLowerCase())
                && savedHash.equals(hash(password));
    }

    public static void setLoggedIn(Context c, boolean loggedIn) {
        sp(c).edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }

    public static boolean isLoggedIn(Context c) {
        return sp(c).getBoolean(KEY_LOGGED_IN, false);
    }

    public static String getUserName(Context c) {
        return sp(c).getString(KEY_NAME, "Guest");
    }

    private static String hash(String s) {
        try {
            java.security.MessageDigest md =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(s.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(s.hashCode());
        }
    }


    // ---------- Favorites ----------

    private static final String KEY_FAVORITES = "favorites";

    public static java.util.Set<String> getFavorites(Context c) {
        return new java.util.HashSet<>(sp(c).getStringSet(KEY_FAVORITES, new java.util.HashSet<>()));
    }

    public static boolean isFavorite(Context c, String name) {
        return getFavorites(c).contains(name);
    }

    /** Toggles favorite status; returns the NEW state. */
    public static boolean toggleFavorite(Context c, String name) {
        java.util.Set<String> set = getFavorites(c);
        boolean nowFav;
        if (set.contains(name)) { set.remove(name); nowFav = false; }
        else { set.add(name); nowFav = true; }
        sp(c).edit().putStringSet(KEY_FAVORITES, set).apply();
        return nowFav;
    }

    // ---------- Festival reminders ----------

    private static final String KEY_NOTIFY = "notify_set";

    public static java.util.Set<String> getNotifySet(Context c) {
        return new java.util.HashSet<>(sp(c).getStringSet(KEY_NOTIFY, new java.util.HashSet<>()));
    }

    public static boolean isNotifyOn(Context c, String name) {
        return getNotifySet(c).contains(name);
    }

    public static void setNotify(Context c, String name, boolean on) {
        java.util.Set<String> set = getNotifySet(c);
        if (on) set.add(name); else set.remove(name);
        sp(c).edit().putStringSet(KEY_NOTIFY, set).apply();
    }



    // ---------- Feedback (stored on device) ----------

    private static final String KEY_FEEDBACK = "feedback_log";

    public static void saveFeedback(Context c, String name, String email, String msg) {
        java.util.Set<String> set = new java.util.HashSet<>(
                sp(c).getStringSet(KEY_FEEDBACK, new java.util.HashSet<>()));
        set.add(System.currentTimeMillis() + "|" + name + "|" + email + "|"
                + msg.replace("|", "/"));
        sp(c).edit().putStringSet(KEY_FEEDBACK, set).apply();
    }

    // ---------- Custom calendar events (recur annually by BS date) ----------

    private static final String KEY_EVENTS = "custom_events";

    /** Entries: "month|day|type|title" (type: B = birthday, E = event). */
    public static java.util.Set<String> getEvents(Context c) {
        return new java.util.HashSet<>(sp(c).getStringSet(KEY_EVENTS, new java.util.HashSet<>()));
    }

    public static void addEvent(Context c, int bsMonth, int bsDay, String type, String title) {
        java.util.Set<String> set = getEvents(c);
        set.add(bsMonth + "|" + bsDay + "|" + type + "|" + title.replace("|", "/"));
        sp(c).edit().putStringSet(KEY_EVENTS, set).apply();
    }

    public static void removeEvent(Context c, String entry) {
        java.util.Set<String> set = getEvents(c);
        set.remove(entry);
        sp(c).edit().putStringSet(KEY_EVENTS, set).apply();
    }

    /** All entries on a given BS month/day. */
    public static java.util.List<String> eventsOn(Context c, int bsMonth, int bsDay) {
        java.util.List<String> out = new java.util.ArrayList<>();
        String prefix = bsMonth + "|" + bsDay + "|";
        for (String e : getEvents(c)) {
            if (e.startsWith(prefix)) out.add(e);
        }
        return out;
    }

    public static boolean isDarkMode(Context c) {
        return sp(c).getBoolean(KEY_DARK_MODE, true);
    }

    public static void setDarkMode(Context c, boolean dark) {
        sp(c).edit().putBoolean(KEY_DARK_MODE, dark).apply();
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES
                     : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void applySavedTheme(Context c) {
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode(c) ? AppCompatDelegate.MODE_NIGHT_YES
                              : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
