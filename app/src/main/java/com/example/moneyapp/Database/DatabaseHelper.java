package com.example.moneyapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "Signup.db";
    private static final String TABLE_USERS = "allusers";
    private static final String TABLE_BUDGET = "budget";
    private static final String TABLE_INCOME = "income";
    private static final String TABLE_SPENDING = "spending";

    public DatabaseHelper(Context context){
        super(context, databaseName, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(email TEXT PRIMARY KEY, password TEXT, profile_image TEXT)");
        db.execSQL(createFinancialTableSql(TABLE_BUDGET));
        db.execSQL(createFinancialTableSql(TABLE_INCOME));
        db.execSQL(createFinancialTableSql(TABLE_SPENDING));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET + "(id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT NOT NULL,date TEXT,amount REAL,details TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INCOME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT NOT NULL,date TEXT,amount REAL,details TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SPENDING + "(id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT NOT NULL,date TEXT,amount REAL,details TEXT)");
        }
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN profile_image TEXT");
            } catch (Exception ignored) {
                // no-op if already exists
            }
        }
    }

    private String createFinancialTableSql(String tableName) {
        return "CREATE TABLE " + tableName + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT NOT NULL," +
                "date TEXT," +
                "amount REAL," +
                "details TEXT" +
                ")";
    }
    public Boolean insertData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);

        long result = db.insert(TABLE_USERS, null, values);

        return result != -1;
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE email = ? AND password = ?",
                new String[]{email, password}
        );

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    public boolean updateUserProfile(String oldEmail, String newEmail, String newPassword, String profileImageUri) {
        if (TextUtils.isEmpty(oldEmail) || TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newPassword)) {
            return false;
        }

        String oldTrimmed = oldEmail.trim();
        String newTrimmed = newEmail.trim();

        if (!oldTrimmed.equalsIgnoreCase(newTrimmed) && checkEmail(newTrimmed)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("email", newTrimmed);
            values.put("password", newPassword.trim());
            values.put("profile_image", profileImageUri == null ? "" : profileImageUri.trim());

            int updated = db.update(TABLE_USERS, values, "email = ?", new String[]{oldTrimmed});
            if (updated <= 0) {
                return false;
            }

            if (!oldTrimmed.equalsIgnoreCase(newTrimmed)) {
                updateFinancialEmail(db, TABLE_BUDGET, oldTrimmed, newTrimmed);
                updateFinancialEmail(db, TABLE_INCOME, oldTrimmed, newTrimmed);
                updateFinancialEmail(db, TABLE_SPENDING, oldTrimmed, newTrimmed);
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }


    public String getPasswordForEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return "";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT password FROM " + TABLE_USERS + " WHERE email = ? LIMIT 1",
                new String[]{email.trim()}
        );

        try {
            if (cursor.moveToFirst()) {
                return valueOrEmpty(cursor.getString(0));
            }
        } finally {
            cursor.close();
        }

        return "";
    }

    public String getProfileImageUri(String email) {
        if (TextUtils.isEmpty(email)) {
            return "";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT profile_image FROM " + TABLE_USERS + " WHERE email = ? LIMIT 1",
                new String[]{email.trim()}
        );

        try {
            if (cursor.moveToFirst()) {
                return valueOrEmpty(cursor.getString(0));
            }
        } finally {
            cursor.close();
        }

        return "";
    }

    private void updateFinancialEmail(SQLiteDatabase db, String tableName, String oldEmail, String newEmail) {
        ContentValues values = new ContentValues();
        values.put("email", newEmail);
        db.update(tableName, values, "email = ?", new String[]{oldEmail});
    }

    public void insertFinancialEntry(String tableName, String email, String date, String amount, String details) {
        if (TextUtils.isEmpty(email) || !isSupportedFinancialTable(tableName)) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String safeDate = date == null ? "" : date.trim();
        String safeAmount = amount == null ? "" : amount.trim();
        String safeDetails = details == null ? "" : details.trim();

        Cursor cursor = db.rawQuery(
                "SELECT date, amount, details FROM " + tableName + " WHERE email = ? ORDER BY id DESC LIMIT 1",
                new String[]{email.trim()}
        );

        try {
            if (cursor.moveToFirst()) {
                String lastDate = cursor.getString(0);
                String lastAmount = cursor.getString(1);
                String lastDetails = cursor.getString(2);
                if (safeDate.equals(valueOrEmpty(lastDate))
                        && safeAmount.equals(valueOrEmpty(lastAmount))
                        && safeDetails.equals(valueOrEmpty(lastDetails))) {
                    return;
                }
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put("email", email.trim());
        values.put("date", safeDate);
        values.put("amount", parseAmount(safeAmount));
        values.put("details", safeDetails);

        db.insert(tableName, null, values);
    }

    public void deleteFinancialEntryByDisplayIndex(String tableName, String email, int displayIndex) {
        if (TextUtils.isEmpty(email) || displayIndex < 0 || !isSupportedFinancialTable(tableName)) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String trimmedEmail = email.trim();

        db.execSQL(
                "DELETE FROM " + tableName +
                        " WHERE id IN (SELECT id FROM " + tableName + " WHERE email = ? ORDER BY id DESC LIMIT 1 OFFSET ?)",
                new Object[]{trimmedEmail, displayIndex}
        );
    }

    public void updateFinancialEntryByDisplayIndex(
            String tableName,
            String email,
            int displayIndex,
            String date,
            String amount,
            String details
    ) {
        if (TextUtils.isEmpty(email) || displayIndex < 0 || !isSupportedFinancialTable(tableName)) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String trimmedEmail = email.trim();
        String safeDate = date == null ? "" : date.trim();
        String safeDetails = details == null ? "" : details.trim();

        ContentValues values = new ContentValues();
        values.put("date", safeDate);
        values.put("amount", parseAmount(amount == null ? "" : amount.trim()));
        values.put("details", safeDetails);

        db.update(
                tableName,
                values,
                "id IN (SELECT id FROM " + tableName + " WHERE email = ? ORDER BY id DESC LIMIT 1 OFFSET ?)",
                new String[]{trimmedEmail, String.valueOf(displayIndex)}
        );
    }

    private boolean isSupportedFinancialTable(String tableName) {
        return TABLE_BUDGET.equals(tableName)
                || TABLE_INCOME.equals(tableName)
                || TABLE_SPENDING.equals(tableName);
    }

    private double parseAmount(String amount) {
        if (TextUtils.isEmpty(amount)) {
            return 0d;
        }
        try {
            return Double.parseDouble(amount);
        } catch (NumberFormatException ignored) {
            return 0d;
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
