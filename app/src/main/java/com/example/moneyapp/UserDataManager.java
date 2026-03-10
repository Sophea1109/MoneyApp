package com.example.moneyapp;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.Map;

public final class UserDataManager {

    private UserDataManager() {
    }

    public static SharedPreferences getPrefs(Context context) {
        String email = SessionManager.getCurrentUser(context);
        return context.getSharedPreferences(getPrefsNameForEmail(email), Context.MODE_PRIVATE);
    }

    public static SharedPreferences getPrefsForEmail(Context context, String email) {
        return context.getSharedPreferences(getPrefsNameForEmail(email), Context.MODE_PRIVATE);
    }

    public static void migrateUserPrefs(Context context, String oldEmail, String newEmail) {
        SharedPreferences source = getPrefsForEmail(context, oldEmail);
        SharedPreferences target = getPrefsForEmail(context, newEmail);

        Map<String, ?> sourceAll = source.getAll();
        SharedPreferences.Editor targetEditor = target.edit();
        targetEditor.clear();

        for (Map.Entry<String, ?> entry : sourceAll.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                targetEditor.putString(key, (String) value);
            } else if (value instanceof Integer) {
                targetEditor.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                targetEditor.putLong(key, (Long) value);
            } else if (value instanceof Float) {
                targetEditor.putFloat(key, (Float) value);
            } else if (value instanceof Boolean) {
                targetEditor.putBoolean(key, (Boolean) value);
            }
        }
        targetEditor.apply();

        source.edit().clear().apply();
    }

    private static String getPrefsNameForEmail(String email) {
        String value = email;
        if (value == null || value.trim().isEmpty()) {
            value = "guest";
        }
            String safeEmail = value.trim().toLowerCase().replaceAll("[^a-z0-9._-]", "_");
            return "MoneyApp_" + safeEmail;
    }
}