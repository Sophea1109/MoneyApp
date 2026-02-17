package com.example.moneyapp;

import android.content.Context;
import android.content.SharedPreferences;
public class SessionManager {
    private static final String PREFS = "MoneyApp";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    private SessionManager() {
    }

    public static boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_LOGGED_IN, false);
    }

    public static void setLoggedIn(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_LOGGED_IN, value).commit();
    }

    public static void clearSession(Context context) {
        setLoggedIn(context, false);
    }
}
