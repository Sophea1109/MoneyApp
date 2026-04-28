package com.example.moneyapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.moneyapp.Database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class HistoryRepository {

    private HistoryRepository() {}

    public static final class HistoryEntry {
        public final String date;
        public final String amount;
        public final String details;

        public HistoryEntry(String date, String amount, String details) {
            this.date    = date;
            this.amount  = amount;
            this.details = details;
        }
    }
    public static void syncFromDatabase(Context context) {
        int userId = SessionManager.getCurrentUserId(context);
        DatabaseHelper db = new DatabaseHelper(context);
        rebuildCache(context, db, "income",      "income",   userId);
        rebuildCache(context, db, "budget",      "budget",   userId);
        rebuildCache(context, db, "transaction", "spending", userId);
    }

    private static void rebuildCache(Context context, DatabaseHelper db,
                                     String type, String tableName, int userId) {
        List<HistoryEntry> entries = db.getFinancialEntries(tableName, userId);
        List<HistoryEntry> ordered = new ArrayList<>();
        for (int i = entries.size() - 1; i >= 0; i--) ordered.add(entries.get(i));

        JSONArray jsonArray = new JSONArray();
        for (HistoryEntry e : ordered) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("date", e.date);
                obj.put("amount", e.amount);
                obj.put("details", e.details);
                jsonArray.put(obj);
            } catch (JSONException ignored) {}
        }

        SharedPreferences prefs = UserDataManager.getPrefs(context);
        prefs.edit().putString(type + "_history", jsonArray.toString()).apply();
        syncCurrentValuesToLatest(prefs, type, jsonArray);
    }

    public static void appendHistoryEntry(Context context, String type,
                                          String date, String amount, String details) {
        if (isBlank(date) && isBlank(amount) && isBlank(details)) return;

        SharedPreferences preferences = UserDataManager.getPrefs(context);
        String key = type + "_history";
        String raw = preferences.getString(key, "[]");

        try {
            JSONArray entries = new JSONArray(raw);
            JSONObject latestEntry = entries.length() > 0
                    ? entries.getJSONObject(entries.length() - 1) : null;

            if (latestEntry != null
                    && date.equals(latestEntry.optString("date", ""))
                    && amount.equals(latestEntry.optString("amount", ""))
                    && details.equals(latestEntry.optString("details", ""))) return;

            JSONObject newEntry = new JSONObject();
            newEntry.put("date", date);
            newEntry.put("amount", amount);
            newEntry.put("details", details);

            entries.put(newEntry);
            preferences.edit().putString(key, entries.toString()).apply();
            syncCurrentValuesToLatest(preferences, type, entries);
        } catch (JSONException ignored) {
            JSONArray entries = new JSONArray();
            try {
                JSONObject newEntry = new JSONObject();
                newEntry.put("date", date);
                newEntry.put("amount", amount);
                newEntry.put("details", details);
                entries.put(newEntry);
                preferences.edit().putString(key, entries.toString()).apply();
                syncCurrentValuesToLatest(preferences, type, entries);
            } catch (JSONException ignoredAgain) {}
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
        } catch (JSONException ignored) {}
        return entries;
    }

    public static boolean deleteHistoryEntry(Context context, String type, int displayIndex) {
        SharedPreferences preferences = UserDataManager.getPrefs(context);
        String key = type + "_history";
        String raw = preferences.getString(key, "[]");
        try {
            JSONArray history = new JSONArray(raw);
            int actualIndex = history.length() - 1 - displayIndex;
            if (actualIndex < 0 || actualIndex >= history.length()) return false;

            JSONArray updated = new JSONArray();
            for (int i = 0; i < history.length(); i++) {
                if (i != actualIndex) updated.put(history.getJSONObject(i));
            }
            preferences.edit().putString(key, updated.toString()).apply();
            syncCurrentValuesToLatest(preferences, type, updated);
            return true;
        } catch (JSONException ignored) {
            return false;
        }
    }

    public static boolean updateHistoryEntry(Context context, String type, int displayIndex,
                                             String newDate, String newAmount, String newDetails) {
        SharedPreferences preferences = UserDataManager.getPrefs(context);
        String key = type + "_history";
        String raw = preferences.getString(key, "[]");
        try {
            JSONArray history = new JSONArray(raw);
            int actualIndex = history.length() - 1 - displayIndex;
            if (actualIndex < 0 || actualIndex >= history.length()) return false;

            JSONObject updatedEntry = new JSONObject();
            updatedEntry.put("date", newDate == null ? "" : newDate.trim());
            updatedEntry.put("amount", newAmount == null ? "" : newAmount.trim());
            updatedEntry.put("details", newDetails == null ? "" : newDetails.trim());

            history.put(actualIndex, updatedEntry);
            preferences.edit().putString(key, history.toString()).apply();
            syncCurrentValuesToLatest(preferences, type, history);
            return true;
        } catch (JSONException ignored) {
            return false;
        }
    }

    public static double getTotalAmount(Context context, String type) {
        SharedPreferences preferences = UserDataManager.getPrefs(context);
        String raw = preferences.getString(type + "_history", "[]");
        double total = 0d;
        try {
            JSONArray history = new JSONArray(raw);
            for (int i = 0; i < history.length(); i++) {
                JSONObject item = history.optJSONObject(i);
                if (item != null) total += toAmount(item.optString("amount", "0"));
            }
        } catch (JSONException ignored) {}
        return total;
    }

    public static double toAmount(String value) {
        if (value == null || value.trim().isEmpty()) return 0d;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ignored) {
            return 0d;
        }
    }

    private static void syncCurrentValuesToLatest(SharedPreferences preferences,
                                                  String type, JSONArray history) {
        if (history == null || history.length() == 0) {
            resetCurrentValues(preferences, type);
            return;
        }
        JSONObject latest = history.optJSONObject(history.length() - 1);
        if (latest == null) {
            resetCurrentValues(preferences, type);
            return;
        }
        String date = latest.optString("date", "");
        String amount = latest.optString("amount", "0.00");
        String details = latest.optString("details", "");

        SharedPreferences.Editor editor = preferences.edit();
        if ("transaction".equals(type)) {
            editor.putString("transaction_date", date)
                    .putString("transaction_value", amount)
                    .putString("transaction_details", details);
        } else if ("income".equals(type)) {
            editor.putString("income_date", date)
                    .putString("income_value", amount)
                    .putString("income_details", details);
        } else if ("budget".equals(type)) {
            editor.putString("budget_date", date)
                    .putString("budget_value", amount)
                    .putString("budget_details", details);
        }
        editor.apply();
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

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}