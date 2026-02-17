package com.example.moneyapp;

import android.content.Context;
import android.content.SharedPreferences;
public final class SessionManager {

    private static final String PREFS = "MoneyApp";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER = "current_user_email";

    private SessionManager() {
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean loggedIn = preferences.getBoolean(KEY_LOGGED_IN, false);
        String currentUser = preferences.getString(KEY_CURRENT_USER, "");
        return loggedIn && currentUser != null && !currentUser.trim().isEmpty();
    }

    public static void setLoggedIn(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_LOGGED_IN, value).commit();
    }

    public static void setCurrentUser(Context context, String email) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_CURRENT_USER, email == null ? "" : email.trim()).commit();
    }

    public static String getCurrentUser(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_CURRENT_USER, "");
    }

    public static void clearSession(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .putString(KEY_CURRENT_USER, "")
                .commit();
    }
}