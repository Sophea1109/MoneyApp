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

    public static void appendHistoryEntry(Context context, String type, String date, String amount, String details) {
        if (isBlank(date) && isBlank(amount) && isBlank(details)) {
            return;
        }

        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
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

    public static List<String> getHistoryLines(Context context, String type) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = preferences.getString(type + "_history", "[]");
        List<String> lines = new ArrayList<>();

        try {
            JSONArray entries = new JSONArray(raw);
            for (int i = entries.length() - 1; i >= 0; i--) {
                JSONObject item = entries.getJSONObject(i);
                String date = item.optString("date", "-");
                String amount = item.optString("amount", "0.00");
                String details = item.optString("details", "-");
                lines.add("Date: " + date + "\nAmount: $" + amount + "\nDetails: " + details);
            }
        } catch (JSONException ignored) {
            // no-op
        }

        return lines;
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
