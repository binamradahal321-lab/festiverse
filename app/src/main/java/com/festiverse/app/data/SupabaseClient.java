package com.festiverse.app.data;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Minimal Supabase client (pure Java, no SDK needed).
 *
 * SETUP — paste your own values from the Supabase dashboard
 * (Project Settings -> API):
 *   1. SUPABASE_URL  = your "Project URL"  (https://xxxx.supabase.co)
 *   2. SUPABASE_ANON = your "anon public" key
 *
 * The anon key is SAFE to ship in the app — it only allows what your
 * Row Level Security policies permit. Never put the service_role key here.
 *
 * Until both values are set, the app automatically falls back to the
 * on-device demo login, so it keeps working.
 */
public class SupabaseClient {

    public static final String SUPABASE_URL = "https://wbybhancoprxnwdtscpf.supabase.co";
    public static final String SUPABASE_ANON = "sb_publishable_PxQ0ooNZgKrdVuoYfd0gkw_UNuXXhR_";

    public interface AuthCallback {
        void onResult(boolean ok, String userNameOrError);
    }

    public interface SimpleCallback {
        void onResult(boolean ok);
    }

    public static boolean isConfigured() {
        return SUPABASE_URL.startsWith("https://") && SUPABASE_ANON.length() > 20;
    }

    // ---------------- Auth ----------------

    /** Creates a Supabase Auth user with the name stored in user metadata. */
    public static void signUp(String name, String email, String password, AuthCallback cb) {
        run(() -> {
            try {
                JSONObject data = new JSONObject().put("full_name", name);
                JSONObject body = new JSONObject()
                        .put("email", email)
                        .put("password", password)
                        .put("data", data);
                JSONObject res = post(SUPABASE_URL + "/auth/v1/signup", body, null);
                if (res.has("id") || res.has("user") || res.has("access_token")) {
                    return new Object[]{true, name};
                }
                return new Object[]{false, errorOf(res)};
            } catch (Exception e) {
                return new Object[]{false, "Network error — check your internet"};
            }
        }, cb);
    }

    /** Signs in with email + password; returns the stored full name. */
    public static void signIn(String email, String password, AuthCallback cb) {
        run(() -> {
            try {
                JSONObject body = new JSONObject()
                        .put("email", email)
                        .put("password", password);
                JSONObject res = post(
                        SUPABASE_URL + "/auth/v1/token?grant_type=password", body, null);
                if (res.has("access_token")) {
                    String name = email;
                    JSONObject user = res.optJSONObject("user");
                    if (user != null) {
                        JSONObject meta = user.optJSONObject("user_metadata");
                        if (meta != null) name = meta.optString("full_name", email);
                    }
                    return new Object[]{true, name};
                }
                return new Object[]{false, errorOf(res)};
            } catch (Exception e) {
                return new Object[]{false, "Network error — check your internet"};
            }
        }, cb);
    }

    // ---------------- Feedback ----------------

    /** Inserts one row into the public.feedback table. */
    public static void sendFeedback(String name, String email, String message,
                                    SimpleCallback cb) {
        Handler main = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            boolean ok = false;
            try {
                JSONObject body = new JSONObject()
                        .put("name", name)
                        .put("email", email)
                        .put("message", message);
                HttpURLConnection conn = open(
                        SUPABASE_URL + "/rest/v1/feedback", "POST");
                conn.setRequestProperty("Prefer", "return=minimal");
                write(conn, body);
                int code = conn.getResponseCode();
                ok = code >= 200 && code < 300;
                conn.disconnect();
            } catch (Exception ignored) { }
            final boolean result = ok;
            main.post(() -> cb.onResult(result));
        }).start();
    }

    // ---------------- Plumbing ----------------

    private interface Task { Object[] run(); }

    private static void run(Task task, AuthCallback cb) {
        Handler main = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            Object[] r = task.run();
            main.post(() -> cb.onResult((Boolean) r[0], (String) r[1]));
        }).start();
    }

    private static HttpURLConnection open(String url, String method) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("apikey", SUPABASE_ANON);
        conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        return conn;
    }

    private static void write(HttpURLConnection conn, JSONObject body) throws Exception {
        OutputStream os = conn.getOutputStream();
        os.write(body.toString().getBytes("UTF-8"));
        os.close();
    }

    private static JSONObject post(String url, JSONObject body, String bearer)
            throws Exception {
        HttpURLConnection conn = open(url, "POST");
        if (bearer != null) {
            conn.setRequestProperty("Authorization", "Bearer " + bearer);
        }
        write(conn, body);
        int code = conn.getResponseCode();
        InputStream is = code < 400 ? conn.getInputStream() : conn.getErrorStream();
        StringBuilder sb = new StringBuilder();
        if (is != null) {
            BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
            r.close();
        }
        conn.disconnect();
        String text = sb.toString().trim();
        if (text.isEmpty()) text = "{}";
        if (text.startsWith("[")) text = "{\"list\":" + text + "}";
        return new JSONObject(text);
    }

    private static String errorOf(JSONObject res) {
        String m = res.optString("msg", "");
        if (m.isEmpty()) m = res.optString("error_description", "");
        if (m.isEmpty()) m = res.optString("message", "");
        if (m.isEmpty()) m = res.optString("error", "Something went wrong");
        return m;
    }
}
