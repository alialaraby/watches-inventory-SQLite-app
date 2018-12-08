package com.alisakralarabygmail.watchesinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.alisakralarabygmail.watchesinventory.data.*;
import com.alisakralarabygmail.watchesinventory.data.WatchesContract.WatchesEntry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class WatchesProvider extends ContentProvider {

    //this constant and its relevant value (code) is used for the whole watches table uri pattern
    private static final int WATCHES_TABLE_URI_CODE = 100;
    //this constant and its relevant value (code) is used for a single row in the table uri pattern
    private static final int WATCHES_ROW_URI_CODE = 200;

    //creating a UriMatcher to filter the incoming uris
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //this static part is first executed whenever this class is used
    //in which we add patterns of the uris we need
    static {

        //this pattern is used for a uri to the whole watches table with the code 100
        uriMatcher.addURI(WatchesContract.CONTENT_AUTHORITY, WatchesContract.WATCHESINVENTORY_PATH, WATCHES_TABLE_URI_CODE);
        //this pattern is used for a uri to a single row in the watches table with the code 200
        uriMatcher.addURI(WatchesContract.CONTENT_AUTHORITY, WatchesContract.WATCHESINVENTORY_PATH + "/#", WATCHES_ROW_URI_CODE);
    }

    private WatchesDbHelper watchesDbHelper;
    @Override
    public boolean onCreate() {

        //initialize a helper instance to get access to the watches database
        watchesDbHelper = new WatchesDbHelper(getContext(), WatchesDbHelper.DATABASE_NAME,
                null, WatchesDbHelper.DATABASE_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //database object to gain access to the watches database through the helper
        SQLiteDatabase sqLiteDatabase = watchesDbHelper.getReadableDatabase();

        //variable that store the result of matching the coming uri with the patterns
        int matcher = uriMatcher.match(uri);

        Cursor cursor;

        //checking the uri with the patterns
        if (matcher == WATCHES_TABLE_URI_CODE){

            //cursor to store the result of the query method
            cursor = sqLiteDatabase.query(WatchesEntry.WATCHES_TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);

        }else if (matcher == WATCHES_ROW_URI_CODE){

            //setting the selection part to define the WHERE clause column as we are in the case of accessing a single pet
            selection = WatchesEntry._ID + "=?";

            //setting the selection part to define the WHERE clause value coming from the uri (the id in this case)
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

            cursor = sqLiteDatabase.query(WatchesEntry.WATCHES_TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);
        }else {return null;}

        //notify that changes were made so that the loader automatically updates the ui
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int matcher = uriMatcher.match(uri);

        //here the path to the whole table is illegible as we insert a brand new row
        //and it`s not reasonable to insert a new one on an existing one
        switch (matcher){
            case WATCHES_TABLE_URI_CODE:
                return insertWatch(uri, contentValues);
            default:
                return null;
        }
    }

    public Uri insertWatch(Uri uri, ContentValues values){

        SQLiteDatabase database = watchesDbHelper.getWritableDatabase();
        long resultRow = database.insert(WatchesEntry.WATCHES_TABLE_NAME, null, values);

        //here we notify that changes were made
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, resultRow);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int matcher = uriMatcher.match(uri);

        switch (matcher){

            case WATCHES_TABLE_URI_CODE:
                return deleteWatch(uri, selection, selectionArgs);
            case WATCHES_ROW_URI_CODE:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = WatchesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return deleteWatch(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("can`t delete");
        }

    }

    public int deleteWatch(Uri uri, String selection, String[] selectionArgs){

        //get a reference to a writable instance of the database
        SQLiteDatabase database = watchesDbHelper.getWritableDatabase();

        int resultRow = database.delete(WatchesEntry.WATCHES_TABLE_NAME, selection, selectionArgs);
        if (resultRow != 0){
            //here we notify that changes were made
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return resultRow;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {


        int matcher = uriMatcher.match(uri);
        switch (matcher){

            case WATCHES_ROW_URI_CODE:
                selection = WatchesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateWatch(uri, selection, selectionArgs, contentValues);
            default:
                throw new IllegalArgumentException("can`t update");
        }
    }

    public int updateWatch(Uri uri, String selection, String[] selectionArgs, ContentValues values){

        SQLiteDatabase database = watchesDbHelper.getWritableDatabase();
        int resultRow = database.update(WatchesEntry.WATCHES_TABLE_NAME, values, selection, selectionArgs);
        if (resultRow != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return resultRow;
    }
}
