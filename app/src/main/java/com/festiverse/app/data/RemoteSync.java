package com.festiverse.app.data;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Live festival-date sync.
 *
 * Hamro Patro has NO public API, so the app cannot legally pull from it
 * directly. Instead, this fetches a small JSON file you host yourself
 * (e.g. a GitHub "raw" link) with the latest verified dates, which you can
 * update any time WITHOUT releasing a new APK. Format:
 *
 *   [ {"name": "Bijaya Dashami (Tika)", "date": "2026-10-21"}, ... ]
 *
 * Names must match the bundled festival names. On any failure the app
 * silently keeps its bundled dataset, so it always works offline.
 */
public class RemoteSync {

    /** Point this at your own hosted JSON (GitHub raw / any static host). */
    public static final String REMOTE_URL =
            "https://raw.githubusercontent.com/YOUR_USERNAME/festiverse-data/main/festivals.json";

    public interface Callback {
        void onDone(boolean updated);
    }

    public static void fetch(Callback cb) {
        Handler main = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            boolean updated = false;
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(REMOTE_URL).openConnection();
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);
                conn.setRequestProperty("User-Agent", "FestiVerse");
                if (conn.getResponseCode() == 200) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line;
                    while ((line = r.readLine()) != null) sb.append(line);
                    r.close();

                    JSONArray arr = new JSONArray(sb.toString());
                    Map<String, String> dates = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        String name = o.optString("name", "");
                        String date = o.optString("date", "");
                        if (!name.isEmpty() && date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            dates.put(name, date);
                        }
                    }
                    if (!dates.isEmpty()) {
                        updated = FestivalRepository.get().applyRemoteDates(dates);
                    }
                }
            } catch (Exception ignored) {
                // Offline or bad data: bundled dataset stays in charge.
            } finally {
                if (conn != null) conn.disconnect();
            }
            final boolean result = updated;
            main.post(() -> cb.onDone(result));
        }).start();
    }
}
