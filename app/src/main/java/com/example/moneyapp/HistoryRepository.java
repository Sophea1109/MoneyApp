package com.example.moneyapp;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class HistoryRepository {

    private static final String PREFS = "MoneyApp";

    private HistoryRepository() {
    }

    public static final class HistoryEntry {
        public final String date;
        public final String amount;
        public final String details;

        public HistoryEntry(String date, String amount, String details) {
            this.date = date;
            this.amount = amount;
            this.details = details;
        }
    }

    public static void appendHistoryEntry(Context context, String type, String date, String amount, String details) {
        if (isBlank(date) && isBlank(amount) && isBlank(details)) {
            return;
        }

        SharedPreferences preferences = UserDataManager.getPrefs(context);
        String key = type + "_history";
        String raw = preferences.getString(key, "[]");

        try {
            JSONArray entries = new JSONArray(raw);
            JSONObject latestEntry = entries.length() > 0 ? entries.getJSONObject(entries.length() - 1) : null;

            if (latestEntry != null
                    && date.equals(latestEntry.optString("date", ""))
                    && amount.equals(latestEntry.optString("amount", ""))
                    && details.equals(latestEntry.optString("details", ""))) {
                return;
            }

            JSONObject newEntry = new JSONObject();
            newEntry.put("date", date);
            newEntry.put("amount", amount);
            newEntry.put("details", details);

            entries.put(newEntry);
            preferences.edit().putString(key, entries.toString()).apply();
        } catch (JSONException ignored) {
            JSONArray entries = new JSONArray();
            JSONObject newEntry = new JSONObject();
            try {
                newEntry.put("date", date);
                newEntry.put("amount", amount);
                newEntry.put("details", details);
                entries.put(newEntry);
                preferences.edit().putString(key, entries.toString()).apply();
            } catch (JSONException ignoredAgain) {
                // no-op
            }
        }
    }

    public static List<HistoryEntry> getHistoryEntries(Context context, String type) {
        SharedPreferences preferences = UserDataManager.getPrefs(context);
        String raw = preferences.getString(type + "_history", "[]");
        List<HistoryEntry> entries = new ArrayList<>();

        try {
            JSONArray history = new JSONArray(raw);
            for (int i = history.length() - 1; i >= 0; i--) {
                JSONObject item = history.getJSONObject(i);
                entries.add(new HistoryEntry(
                        item.optString("date", "-"),
                        item.optString("amount", "0.00"),
                        item.optString("details", "-")
                ));
            }
        } catch (JSONException ignored) {
            // no-op
        }

        return entries;
    }

    public static boolean deleteHistoryEntry(Context context, String type, int displayIndex) {
        SharedPreferences preferences = UserDataManager.getPrefs(context);
        String key = type + "_history";
        String raw = preferences.getString(key, "[]");

        try {
            JSONArray history = new JSONArray(raw);
            int actualIndex = history.length() - 1 - displayIndex;
            if (actualIndex < 0 || actualIndex >= history.length()) {
                return false;
            }

            boolean deletedLatestEntry = actualIndex == history.length() - 1;
            JSONArray updated = new JSONArray();
            for (int i = 0; i < history.length(); i++) {
                if (i != actualIndex) {
                    updated.put(history.getJSONObject(i));
                }
            }

            preferences.edit().putString(key, updated.toString()).apply();

            if (updated.length() == 0 || deletedLatestEntry) {
                resetCurrentValues(preferences, type);
            }

            return true;
        } catch (JSONException ignored) {
            return false;
        }
    }

    private static void resetCurrentValues(SharedPreferences preferences, String type) {
        SharedPreferences.Editor editor = preferences.edit();

        if ("transaction".equals(type)) {
            editor.putString("transaction_date", "")
                    .putString("transaction_value", "0.00")
                    .putString("transaction_details", "");
        } else if ("income".equals(type)) {
            editor.putString("income_date", "")
                    .putString("income_value", "0.00")
                    .putString("income_details", "");
        } else if ("budget".equals(type)) {
            editor.putString("budget_date", "")
                    .putString("budget_value", "0.00")
                    .putString("budget_details", "");
        }

        editor.apply();
    }

    public static double toAmount(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0d;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ignored) {
            return 0d;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}