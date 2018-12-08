package com.alisakralarabygmail.watchesinventory;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.alisakralarabygmail.watchesinventory.data.WatchesContract.*;
import com.alisakralarabygmail.watchesinventory.data.WatchesDbHelper;

import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView listView;
    GridView gridView;
    WatchesDbHelper watchesDbHelper;
    WatchesCursorAdapter cursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initializing the listview
        //listView = (ListView) findViewById(R.id.watches_listview);

        gridView = (GridView) findViewById(R.id.grid_view);

        //hooking up the empty view to the list view whenever it`s empty
        //listView.setEmptyView(findViewById(R.id.empty_view));

        gridView.setEmptyView(findViewById(R.id.empty_view));

        //get an instance of the database helper for creating and accessing one
        watchesDbHelper = new WatchesDbHelper(this, WatchesDbHelper.DATABASE_NAME, null, WatchesDbHelper.DATABASE_VERSION);

        /*
        //a cursor to hold the data retrieved from querying the database
        Cursor result = retrieveData();
        */

        //initializing the cursor adapter
        cursorAdapter = new WatchesCursorAdapter(this, null);

        //setting up the listview with the cursoradapter
        //listView.setAdapter(cursorAdapter);

        gridView.setAdapter(cursorAdapter);

        //initialize the loader manager for creating and using the loader in this activity
        getLoaderManager().initLoader(0, null, this);

        /*
        //setting action for clicking each listview item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                //setting up an intent for the adding activity to make use of it on updating a watch
                Intent intent = new Intent(HomeActivity.this, AddingActivity.class);

                //create a uri with data of the clicked item to send with the intent
                //so that in the adding activity we can extract the data
                Uri uri = ContentUris.withAppendedId(WatchesEntry.CONTENT_URI, id);

                //send the uri with the intent
                intent.setData(uri);
                startActivity(intent);
            }
        });
        */

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                //setting up an intent for the adding activity to make use of it on updating a watch
                Intent intent = new Intent(HomeActivity.this, AddingActivity.class);

                //create a uri with data of the clicked item to send with the intent
                //so that in the adding activity we can extract the data
                Uri uri = ContentUris.withAppendedId(WatchesEntry.CONTENT_URI, id);

                //send the uri with the intent
                intent.setData(uri);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), AddingActivity.class));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
            getContentResolver().delete(WatchesEntry.CONTENT_URI, null, null);
            return true;
        }else {
            return true;
        }

    }

    //method to query the database and retrieve data
    public Cursor retrieveData(){

        String[] projection = {WatchesEntry._ID, WatchesEntry.WATCHES_COLUMN_NAME, WatchesEntry.WATCHES_COLUMN_TYPE,
                WatchesEntry.WATCHES_COLUMN_QUANTITY, WatchesEntry.WATCHES_COLUMN_PRICE};
        return getContentResolver().query(WatchesEntry.CONTENT_URI, projection, null, null, null);
    }

    //method to convert bitmap to byte array
    //as we need to convert our image into byte array first to store it in the database
    //this method is used whenever we insert images to the database
    public byte[] bitMapToByteArray(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    //method to convert byte array into bitmap
    //as we need to convert it first before we show it on an imageview
    //this method is used whenever we retrieve images from the database
    public Bitmap byteArrayToBitmap(byte[] image){

        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //set the projection
        String[] projection = {WatchesEntry._ID, WatchesEntry.WATCHES_COLUMN_NAME
                , WatchesEntry.WATCHES_COLUMN_PICTURE
                , WatchesEntry.WATCHES_COLUMN_TYPE
                , WatchesEntry.WATCHES_COLUMN_QUANTITY
                , WatchesEntry.WATCHES_COLUMN_PRICE};

        return new CursorLoader(getApplicationContext(), WatchesEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //pass the returned cursor"the one holding the retrieved data" from the onCreateLoader
        // to the adapter
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }
}
