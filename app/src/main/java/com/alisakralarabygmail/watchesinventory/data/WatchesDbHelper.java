package com.alisakralarabygmail.watchesinventory.data;

import android.content.Context;
import com.alisakralarabygmail.watchesinventory.data.WatchesContract.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class WatchesDbHelper extends SQLiteOpenHelper {

    //constant for the database name
    public static final String DATABASE_NAME = "watches.db";
    //constant for the database version
    public static final int DATABASE_VERSION = 1;

    //constant for the CREATE TABLE command
    private final String CREATE_TABLE_COMMAND = "CREATE TABLE " + WatchesEntry.WATCHES_TABLE_NAME +
            "(" + WatchesEntry._ID + " INTEGER PRIMARY KEY" + "," +
            WatchesEntry.WATCHES_COLUMN_NAME + " TEXT" + "," +
            WatchesEntry.WATCHES_COLUMN_PICTURE + " BLOB NOT NULL" + "," +
            WatchesEntry.WATCHES_COLUMN_TYPE + " INTEGER" + "," +
            WatchesEntry.WATCHES_COLUMN_QUANTITY + " INTEGER" + "," +
            WatchesEntry.WATCHES_COLUMN_PRICE + " INTEGER" + "," +
            WatchesEntry.WATCHES_COLUMN_SUPPLIER_NAME + " TEXT" + "," +
            WatchesEntry.WATCHES_COLUMN_SUPPLIER_WEBSITE + " TEXT" + ")";

    //constant for the DROP TABLE command
    private final String DROP_TABLE_COMMAND = "DROP TABLE IF EXISTS " + WatchesEntry.WATCHES_TABLE_NAME;

    public WatchesDbHelper(@Nullable Context context, @Nullable String name,
                           @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(DROP_TABLE_COMMAND);
        onCreate(sqLiteDatabase);
    }
}
