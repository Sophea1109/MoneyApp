package com.example.moneyapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.moneyapp.HistoryRepository;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "Signup.db";
    private static final String TABLE_USERS    = "allusers";
    private static final String TABLE_BUDGET   = "budget";
    private static final String TABLE_INCOME   = "income";
    private static final String TABLE_SPENDING = "spending";
    public static final int MIN_PASSWORD_LENGTH = 6;

    private static final int DB_VERSION = 10;

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "salt TEXT," +
                "profile_image TEXT," +
                "created_at TEXT," +
                "updated_at TEXT" +
                ")");
        db.execSQL(createFinancialTableSql(TABLE_BUDGET));
        db.execSQL(createFinancialTableSql(TABLE_INCOME));
        db.execSQL(createFinancialTableSql(TABLE_SPENDING));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT NOT NULL,date TEXT,amount REAL,details TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INCOME +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT NOT NULL,date TEXT,amount REAL,details TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SPENDING +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT NOT NULL,date TEXT,amount REAL,details TEXT)");
        }
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN profile_image TEXT");
            } catch (Exception ignored) {}
        }
        if (oldVersion < 6) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "_new(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "email TEXT UNIQUE," +
                    "password TEXT," +
                    "profile_image TEXT" +
                    ")");
            db.execSQL("INSERT OR IGNORE INTO " + TABLE_USERS + "_new(email, password, profile_image) " +
                    "SELECT email, password, COALESCE(profile_image, '') FROM " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("ALTER TABLE " + TABLE_USERS + "_new RENAME TO " + TABLE_USERS);
        }
        if (oldVersion < 7) {
            addColumnIfMissing(db, TABLE_USERS, "created_at TEXT");
            addColumnIfMissing(db, TABLE_USERS, "updated_at TEXT");
            addColumnIfMissing(db, TABLE_BUDGET, "created_at TEXT");
            addColumnIfMissing(db, TABLE_BUDGET, "updated_at TEXT");
            addColumnIfMissing(db, TABLE_INCOME, "created_at TEXT");
            addColumnIfMissing(db, TABLE_INCOME, "updated_at TEXT");
            addColumnIfMissing(db, TABLE_SPENDING, "created_at TEXT");
            addColumnIfMissing(db, TABLE_SPENDING, "updated_at TEXT");
            backfillTimestamps(db, TABLE_USERS);
            backfillTimestamps(db, TABLE_BUDGET);
            backfillTimestamps(db, TABLE_INCOME);
            backfillTimestamps(db, TABLE_SPENDING);
        }
        if (oldVersion < 8) {
            normalizeLegacyTimestamps(db, TABLE_USERS);
            normalizeLegacyTimestamps(db, TABLE_BUDGET);
            normalizeLegacyTimestamps(db, TABLE_INCOME);
            normalizeLegacyTimestamps(db, TABLE_SPENDING);
        }
        if (oldVersion < 9) {
            addColumnIfMissing(db, TABLE_BUDGET,   "user_id INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(db, TABLE_INCOME,   "user_id INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(db, TABLE_SPENDING, "user_id INTEGER NOT NULL DEFAULT 0");
            for (String table : new String[]{TABLE_BUDGET, TABLE_INCOME, TABLE_SPENDING}) {
                db.execSQL("UPDATE " + table + " SET user_id = (" +
                        "SELECT id FROM " + TABLE_USERS +
                        " WHERE " + TABLE_USERS + ".email = " + table + ".email" +
                        ") WHERE user_id = 0");
            }
        }
        if (oldVersion < 10) {
            addColumnIfMissing(db, TABLE_USERS, "salt TEXT");
        }
    }

    //insert new user.
    public Boolean insertData(String email, String password) {
        if (!isPasswordValid(password)) return false;

        String salt = generateSalt();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", safeEmail(email));
        values.put("password", hashPassword(password, salt));
        values.put("salt", salt);
        values.put("profile_image", "");
        values.put("created_at", currentTimestamp());
        values.put("updated_at", currentTimestamp());

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE email = ?",
                new String[]{safeEmail(email)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT password, salt FROM " + TABLE_USERS + " WHERE email = ? LIMIT 1",
                new String[]{safeEmail(email)});
        try {
            if (!cursor.moveToFirst()) return false;
            String stored = valueOrEmpty(cursor.getString(0));
            String salt   = valueOrEmpty(cursor.getString(1));
            String raw    = password == null ? "" : password.trim();

            if (!salt.isEmpty()) {
                return stored.equals(hashPassword(raw, salt));
            }
            if (stored.equals(hashPasswordSha256(raw))) {
                migrateToSaltedHash(db, safeEmail(email), raw);
                return true;
            }
            if (stored.equals(raw)) {
                migrateToSaltedHash(db, safeEmail(email), raw);
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }

    private void migrateToSaltedHash(SQLiteDatabase db, String email, String rawPassword) {
        String newSalt = generateSalt();
        ContentValues values = new ContentValues();
        values.put("password", hashPassword(rawPassword, newSalt));
        values.put("salt", newSalt);
        values.put("updated_at", currentTimestamp());
        db.update(TABLE_USERS, values, "email = ?", new String[]{email});
    }

    public boolean verifyPassword(String email, String password) {
        return checkEmailPassword(email, password);
    }

    public int getUserIdByEmail(String email) {
        if (TextUtils.isEmpty(email)) return 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USERS + " WHERE email = ? LIMIT 1",
                new String[]{safeEmail(email)});
        try {
            if (cursor.moveToFirst()) return cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return 0;
    }

    public boolean updateUserProfile(String oldEmail, String newEmail,
                                     String newPassword, String profileImageUri) {
        if (TextUtils.isEmpty(oldEmail) || TextUtils.isEmpty(newEmail) ||
                TextUtils.isEmpty(newPassword)) return false;

        if (!isPasswordValid(newPassword)) return false;

        String oldTrimmed = safeEmail(oldEmail);
        String newTrimmed = safeEmail(newEmail);

        if (!oldTrimmed.equalsIgnoreCase(newTrimmed) && checkEmail(newTrimmed)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String newSalt = generateSalt();
            ContentValues values = new ContentValues();
            values.put("email", newTrimmed);
            values.put("password", hashPassword(newPassword.trim(), newSalt));
            values.put("salt", newSalt);
            values.put("profile_image", profileImageUri == null ? "" : profileImageUri.trim());
            values.put("updated_at", currentTimestamp());

            int updated = db.update(TABLE_USERS, values, "email = ?", new String[]{oldTrimmed});
            if (updated <= 0) return false;

            // Keep the email column in sync on financial rows
            // (user_id stays the same — the FK link never breaks)
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

    public String getProfileImageUri(String email) {
        if (TextUtils.isEmpty(email)) return "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT profile_image FROM " + TABLE_USERS + " WHERE email = ? LIMIT 1",
                new String[]{safeEmail(email)});
        try {
            if (cursor.moveToFirst()) return valueOrEmpty(cursor.getString(0));
        } finally {
            cursor.close();
        }
        return "";
    }

    public void insertFinancialEntry(String tableName, int userId, String email,
                                     String date, String amount, String details) {
        if (userId <= 0 || TextUtils.isEmpty(email) || !isSupportedFinancialTable(tableName)) return;

        SQLiteDatabase db  = this.getWritableDatabase();
        String safeDate    = date    == null ? "" : date.trim();
        if (!isValidDate(safeDate)) return;
        String safeAmount  = amount  == null ? "" : amount.trim();
        String safeDetails = details == null ? "" : details.trim();
        String ownerEmail  = safeEmail(email);

        // Duplicate-guard: skip if identical to the most recent row for this user
        Cursor cursor = db.rawQuery(
                "SELECT date, amount, details FROM " + tableName +
                        " WHERE user_id = ? ORDER BY id DESC LIMIT 1",
                new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                if (safeDate.equals(valueOrEmpty(cursor.getString(0)))
                        && safeAmount.equals(valueOrEmpty(cursor.getString(1)))
                        && safeDetails.equals(valueOrEmpty(cursor.getString(2)))) return;
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put("user_id", userId);      // FK — links to allusers.id
        values.put("email", ownerEmail);  // kept for readability & cascade
        values.put("date", safeDate);
        values.put("amount", parseAmount(safeAmount));
        values.put("details", safeDetails);
        values.put("created_at", currentTimestamp());
        values.put("updated_at", currentTimestamp());
        db.insert(tableName, null, values);
    }

    public boolean deleteUserAccount(int userId) {
        if (userId <= 0) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String[] arg = new String[]{String.valueOf(userId)};
            db.delete(TABLE_SPENDING, "user_id = ?", arg);
            db.delete(TABLE_INCOME,  "user_id = ?", arg);
            db.delete(TABLE_BUDGET,  "user_id = ?", arg);
            db.delete(TABLE_USERS,   "id = ?",      arg);
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public void clearFinancialEntriesForUser(String tableName, int userId) {
        if (userId <= 0 || !isSupportedFinancialTable(tableName)) return;
        getWritableDatabase().delete(tableName, "user_id = ?",
                new String[]{String.valueOf(userId)});
    }

    public void deleteFinancialEntryByDisplayIndex(String tableName, int userId, int displayIndex) {
        if (userId <= 0 || displayIndex < 0 || !isSupportedFinancialTable(tableName)) return;
        getWritableDatabase().execSQL(
                "DELETE FROM " + tableName +
                        " WHERE id IN (SELECT id FROM " + tableName +
                        " WHERE user_id = ? ORDER BY id DESC LIMIT 1 OFFSET ?)",
                new Object[]{userId, displayIndex});
    }

    public void updateFinancialEntryByDisplayIndex(String tableName, int userId,
                                                   int displayIndex, String date,
                                                   String amount, String details) {
        if (userId <= 0 || displayIndex < 0 || !isSupportedFinancialTable(tableName)) return;

        ContentValues values = new ContentValues();
        values.put("date",       date    == null ? "" : date.trim());
        values.put("amount", parseAmount(amount == null ? "" : amount.trim()));
        values.put("details",    details == null ? "" : details.trim());
        values.put("updated_at", currentTimestamp());

        getWritableDatabase().update(
                tableName, values,
                "id IN (SELECT id FROM " + tableName +
                        " WHERE user_id = ? ORDER BY id DESC LIMIT 1 OFFSET ?)",
                new String[]{String.valueOf(userId), String.valueOf(displayIndex)});
    }

    public List<HistoryRepository.HistoryEntry> getFinancialEntries(String tableName, int userId) {
        List<HistoryRepository.HistoryEntry> entries = new ArrayList<>();
        if (userId <= 0 || !isSupportedFinancialTable(tableName)) return entries;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT date, amount, details FROM " + tableName +
                        " WHERE user_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(userId)});
        try {
            while (cursor.moveToNext()) {
                entries.add(new HistoryRepository.HistoryEntry(
                        valueOrEmpty(cursor.getString(0)),
                        valueOrEmpty(cursor.getString(1)),
                        valueOrEmpty(cursor.getString(2))
                ));
            }
        } finally {
            cursor.close();
        }
        return entries;
    }

    public double getTotalAmount(String tableName, int userId) {
        if (userId <= 0 || !isSupportedFinancialTable(tableName)) return 0d;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(amount), 0) FROM " + tableName + " WHERE user_id = ?",
                new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) return cursor.getDouble(0);
        } finally {
            cursor.close();
        }
        return 0d;
    }

    //validation
    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= MIN_PASSWORD_LENGTH;
    }

    private void addColumnIfMissing(SQLiteDatabase db, String tableName, String columnDefinition) {
        String columnName = columnDefinition.split(" ")[0];
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        try {
            while (cursor.moveToNext()) {
                if (columnName.equalsIgnoreCase(cursor.getString(1))) return;
            }
        } finally {
            cursor.close();
        }
        db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnDefinition);
    }

    private void backfillTimestamps(SQLiteDatabase db, String tableName) {
        String now = currentTimestamp();
        db.execSQL("UPDATE " + tableName +
                        " SET created_at = COALESCE(NULLIF(created_at, ''), ?)," +
                        " updated_at = COALESCE(NULLIF(updated_at, ''), ?)",
                new Object[]{now, now});
    }

    private void normalizeLegacyTimestamps(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery(
                "SELECT id, created_at, updated_at FROM " + tableName, null);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String createdAt = normalizeTimestampValue(cursor.getString(1));
                String updatedAt = normalizeTimestampValue(cursor.getString(2));
                ContentValues values = new ContentValues();
                values.put("created_at", createdAt);
                values.put("updated_at", updatedAt);
                db.update(tableName, values, "id = ?", new String[]{String.valueOf(id)});
            }
        } finally {
            cursor.close();
        }
    }

    private String normalizeTimestampValue(String value) {
        String trimmed = valueOrEmpty(value).trim();
        if (TextUtils.isEmpty(trimmed)) return currentTimestamp();
        if (!trimmed.matches("\\d{13}")) return trimmed;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.format(new Date(Long.parseLong(trimmed)));
        } catch (NumberFormatException ignored) {
            return trimmed;
        }
    }

    private String createFinancialTableSql(String tableName) {
        return "CREATE TABLE " + tableName + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL DEFAULT 0," +
                "email TEXT NOT NULL," +
                "date TEXT," +
                "amount REAL," +
                "details TEXT," +
                "created_at TEXT," +
                "updated_at TEXT," +
                "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(id)" +
                ")";
    }

    private void updateFinancialEmail(SQLiteDatabase db, String tableName,
                                      String oldEmail, String newEmail) {
        ContentValues values = new ContentValues();
        values.put("email",      newEmail);
        values.put("updated_at", currentTimestamp());
        db.update(tableName, values, "email = ?", new String[]{oldEmail});
    }

    private boolean isSupportedFinancialTable(String tableName) {
        return TABLE_BUDGET.equals(tableName)
                || TABLE_INCOME.equals(tableName)
                || TABLE_SPENDING.equals(tableName);
    }

    private double parseAmount(String amount) {
        if (TextUtils.isEmpty(amount)) return 0d;
        try {
            return Double.parseDouble(amount);
        } catch (NumberFormatException ignored) {
            return 0d;
        }
    }

    private String hashPassword(String password, String salt) {
        try {
            char[] chars = (password == null ? "" : password).toCharArray();
            PBEKeySpec spec = new PBEKeySpec(chars, salt.getBytes(), 10000, 160);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            spec.clearPassword();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception ignored) {
            return hashPasswordSha256(password);
        }
    }

    private String hashPasswordSha256(String password) {
        String raw = password == null ? "" : password.trim();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception ignored) {
            return raw;
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static boolean isValidDate(String date) {
        return date != null && date.trim().matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private String currentTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date());
    }

    private String safeEmail(String email) {
        return email == null ? "" : email.trim();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}