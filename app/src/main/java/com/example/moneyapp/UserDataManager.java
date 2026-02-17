package com.example.moneyapp;
import android.content.Context;
import android.content.SharedPreferences;

public final class UserDataManager {

    private UserDataManager() {
    }

    public static SharedPreferences getPrefs(Context context) {
        String email = SessionManager.getCurrentUser(context);
        if (email == null || email.trim().isEmpty()) {
            email = "guest";
        }
        String safeEmail = email.trim().toLowerCase().replaceAll("[^a-z0-9._-]", "_");
        return context.getSharedPreferences("MoneyApp_" + safeEmail, Context.MODE_PRIVATE);
    }
}