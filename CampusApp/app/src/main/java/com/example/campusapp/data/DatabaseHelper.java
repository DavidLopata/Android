package com.example.campusapp.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusapp.model.LocationItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campusNavigator.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";

    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    public static final String TABLE_FAVORITES = "favorites";

    public static final String COL_LOCATION_ID = "location_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_USERNAME + " TEXT UNIQUE, " +
                        COL_PASSWORD + " TEXT)";

        String createFavoritesTable =
                "CREATE TABLE " + TABLE_FAVORITES + " (" +
                        COL_USERNAME + " TEXT, " +
                        COL_LOCATION_ID + " INTEGER, " +
                        "PRIMARY KEY (" + COL_USERNAME + ", " + COL_LOCATION_ID + "))";

        db.execSQL(createUsersTable);
        db.execSQL(createFavoritesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }


    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                COL_USERNAME + "=?",
                new String[]{username},
                null, null, null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public void addToFavorites(String username, int locationId) {
        if (username == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_LOCATION_ID, locationId);
        db.insertWithOnConflict(
                TABLE_FAVORITES,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }
    public void removeFavorite(String username, int locationId) {
        if (username == null) return;
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_FAVORITES,
                COL_USERNAME + "=? AND " + COL_LOCATION_ID + "=?",
                new String[]{username, String.valueOf(locationId)}
        );
    }

    public boolean isFavorite(String username, int locationId) {
        if (username == null) return false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_FAVORITES,
                null,
                COL_USERNAME + "=? AND " + COL_LOCATION_ID + "=?",
                new String[]{username, String.valueOf(locationId)},
                null, null, null
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<Integer> getFavoritesForUser(String username) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_FAVORITES,
                new String[]{COL_LOCATION_ID},
                COL_USERNAME + "=?",
                new String[]{username},
                null, null, null
        );

        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(0));
        }

        cursor.close();
        return ids;
    }




}
