package com.alisakralarabygmail.watchesinventory;

import com.alisakralarabygmail.watchesinventory.data.WatchesContract.*;
import com.alisakralarabygmail.watchesinventory.data.WatchesDbHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class WatchesCursorAdapter extends CursorAdapter {
    public WatchesCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    WatchesDbHelper dbHelper ;
    int quantityAsInteger = 0;
    String quantityAsString = "";
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        dbHelper = new WatchesDbHelper(context, WatchesDbHelper.DATABASE_NAME, null, WatchesDbHelper.DATABASE_VERSION);
        return LayoutInflater.from(context).inflate(R.layout.watches_list_item_layout_grid_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        //variable to store the string value of the column type value
        String watchTypeAsString = "";

        //variable containing the price
        String priceValue = "";

        ImageView imageView = (ImageView)view.findViewById(R.id.grid_view_adding_image_view);
        TextView name = (TextView) view.findViewById(R.id.grid_view_adding_name_text_view);
        TextView type = (TextView) view.findViewById(R.id.grid_view_adding_type_text_view);
        TextView quantity = (TextView) view.findViewById(R.id.grid_view_adding_quantity_text_view);
        TextView price = (TextView) view.findViewById(R.id.grid_view_adding_price_text_view);


        /*
        //variable to get the image from the cursor and convert it from byte[] to bitmap
        Bitmap bitmap = byteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_PICTURE)));

        imageView.setImageBitmap(bitmap);
        */

        byte[] bytes = cursor.getBlob(cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_PICTURE));
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);

        name.setText(cursor.getString(cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_NAME)));

        //variable to get the integer value of the column type value
        int watchTypeAsInteger = cursor.getInt(cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_TYPE));
        //check the cases of the column type value
        //if 0 set the type text view to show leather
        //if 1 set the type text view to show stainless steel
        //if 2 set the type text view to show rubber
        switch (watchTypeAsInteger){

            case 0:
                watchTypeAsString = "Unknown";
                break;
            case 1:
                watchTypeAsString = "Leather";
                break;
            case 2:
                watchTypeAsString = "Stainless Steel";
                break;
            default:
                watchTypeAsString = "Rubber";
        }

        type.setText(watchTypeAsString);

        quantityAsInteger = cursor.getInt(cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_QUANTITY));

        quantityAsString = String.valueOf(quantityAsInteger) + " left";

        quantity.setText(quantityAsString);

        //getting the price value from the returned cursor and appending it with the dollar sign
        priceValue = String.valueOf(cursor.getInt(cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_PRICE))) + " $";

        price.setText(priceValue);
    }

    //method to convert byte array into bitmap
    //as we need to convert it first before we show it on an imageview
    //this method is used whenever we retrieve images from the database
    public Bitmap byteArrayToBitmap(byte[] image){

        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
