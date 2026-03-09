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
        super(context, databaseName, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(email TEXT PRIMARY KEY, password TEXT)");
        db.execSQL(createFinancialTableSql(TABLE_BUDGET));
        db.execSQL(createFinancialTableSql(TABLE_INCOME));
        db.execSQL(createFinancialTableSql(TABLE_SPENDING));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPENDING);
        onCreate(db);
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
                new String[]{email}
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
