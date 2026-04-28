package com.example.moneyapp;

import android.content.Context;
import android.content.SharedPreferences;
public final class SessionManager {

    private static final String PREFS = "MoneyApp";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER = "current_user_email";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_LOGIN_TIME = "login_timestamp";
    private static final long SESSION_DURATION_MS = 30L * 24 * 60 * 60 * 1000; // 30 days

    private SessionManager() {
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean loggedIn = preferences.getBoolean(KEY_LOGGED_IN, false);
        String currentUser = preferences.getString(KEY_CURRENT_USER, "");
        int currentUserId = preferences.getInt(KEY_CURRENT_USER_ID, 0);
        if (!loggedIn || currentUser == null || currentUser.trim().isEmpty() || currentUserId <= 0) {
            return false;
        }
        long loginTime = preferences.getLong(KEY_LOGIN_TIME, 0);
        if (loginTime > 0 && System.currentTimeMillis() - loginTime > SESSION_DURATION_MS) {
            clearSession(context);
            return false;
        }
        return true;
    }

    public static void setLoggedIn(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit().putBoolean(KEY_LOGGED_IN, value);
        if (value) editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.commit();
    }

    public static void setCurrentUser(Context context, String email) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_CURRENT_USER, email == null ? "" : email.trim()).commit();
    }

    public static String getCurrentUser(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_CURRENT_USER, "");
    }

    public static void setCurrentUserId(Context context, int userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().putInt(KEY_CURRENT_USER_ID, userId).commit();
    }

    public static int getCurrentUserId(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_CURRENT_USER_ID, 0);
    }

    public static void clearSession(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .putString(KEY_CURRENT_USER, "")
                .putInt(KEY_CURRENT_USER_ID, 0)
                .putLong(KEY_LOGIN_TIME, 0)
                .commit();
    }
}