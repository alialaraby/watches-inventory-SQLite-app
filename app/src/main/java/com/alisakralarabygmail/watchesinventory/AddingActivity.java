package com.alisakralarabygmail.watchesinventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alisakralarabygmail.watchesinventory.data.WatchesContract.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText nameEditText, quantityEditText, priceEditText, supplierEditText, supplierWebsiteEditText;
    Button btnInsert;
    ImageButton plusImgBtn, minusImgBtn;
    ImageView imageView;
    Spinner mGenderSpinner;
    int watchType = 0, quantity = 0, price = 0;
    Uri receivedUri = null;

    private static final int PICK_IMAGE = 0;
    Uri selectedImageUri = null;
    byte[] retreivedImageBytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        imageView = (ImageView) findViewById(R.id.add_item_image_view);
        nameEditText = (EditText) findViewById(R.id.add_item_name_edit_text);
        quantityEditText = (EditText) findViewById(R.id.add_item_quantity_edit_text);
        priceEditText = (EditText) findViewById(R.id.add_item_price_edit_text);
        supplierEditText = (EditText) findViewById(R.id.add_item_supplier_edit_text);
        supplierWebsiteEditText = (EditText) findViewById(R.id.add_item_supplier_website_edit_text);
        mGenderSpinner = (Spinner) findViewById(R.id.add_item_type_spinner);

        btnInsert = (Button) findViewById(R.id.add_item_insert_button);
        plusImgBtn = (ImageButton) findViewById(R.id.add_item_btn_plus);
        minusImgBtn = (ImageButton) findViewById(R.id.add_item_btn_minus);

        //here we receive the correct intent
        //weather from the Fab button to insert a new watch
        //or from the listview items to update an existing watch
        Intent intent = getIntent();
        receivedUri = intent.getData();

        if (receivedUri == null){

            setTitle("Insert Watch");
        }else {

            setTitle("Update Watch");
            getLoaderManager().initLoader(0, null, this);
        }

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(nameEditText.getText().toString())){
                    nameEditText.setError("can`t leave empty");
                }else if (!isValidNumber(quantityEditText.getText().toString().trim())){
                    quantityEditText.setError("enter a valid number");
                }else if (!isValidNumber(priceEditText.getText().toString().trim())){
                    priceEditText.setError("enter a valid number");
                }else if (TextUtils.isEmpty(supplierEditText.getText().toString())){
                    supplierEditText.setError("can`t leave empty");
                }else if (TextUtils.isEmpty(supplierWebsiteEditText.getText().toString())){
                    supplierWebsiteEditText.setError("can`t leave empty");
                }else {
                    try {
                        if (receivedUri == null){
                            insertData();
                        }else {
                            updateData();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    leaveActivity();
                }
            }
        });

        minusImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 1){
                    minusImgBtn.setVisibility(View.VISIBLE);
                    quantity--;
                }else {
                    minusImgBtn.setVisibility(View.INVISIBLE);
                }
                quantityEditText.setText(String.valueOf(quantity));
            }
        });

        plusImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                minusImgBtn.setVisibility(View.VISIBLE);
                quantityEditText.setText(String.valueOf(quantity));
            }
        });

        //when the imageButton is clicked a dialog is shown for the user to choose
        //to select an image from gallery or take a picture
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        setupSpinner();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){

            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    public String getPath(Uri uri){

        if (uri == null)
            return null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null){
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(index);
        }
        return uri.getPath();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (receivedUri != null){
            getMenuInflater().inflate(R.menu.menu_delete_adding_activity, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_insert_dummy_adding_activity, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_insert_random:
                insertDummyData();
                leaveActivity();
                return true;
            case R.id.action_delete_current:

                deleteCurrentItem(receivedUri);
                leaveActivity();
                return true;
            default:
                return true;
        }
    }

    //this method is for inserting dummy data
    public void insertDummyData(){

        ContentValues values = new ContentValues();
        Uri resultUri = null;
        long resultRow = 0;

        //get the image from the drawable folder
        @SuppressLint({"NewApi", "LocalSuppress"}) Drawable drawable = getDrawable(R.drawable.empty_store);
        //store the drawable image as bitmap
        Bitmap bitmap =((BitmapDrawable)drawable).getBitmap();
        //stream to convert the image as a stream of characters to store as byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //compress the image if needed
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        //store the image as byte[] array from the stream to store it as blob
        byte[] bytes = stream.toByteArray();

        values.put(WatchesEntry.WATCHES_COLUMN_NAME, "watch A");
        values.put(WatchesEntry.WATCHES_COLUMN_PICTURE, bytes);
        values.put(WatchesEntry.WATCHES_COLUMN_TYPE, WatchesEntry.WATCHES_TYPE_LEATHER);
        values.put(WatchesEntry.WATCHES_COLUMN_QUANTITY, 5);
        values.put(WatchesEntry.WATCHES_COLUMN_TYPE, WatchesEntry.WATCHES_TYPE_STAINLESS_STEEL);
        values.put(WatchesEntry.WATCHES_COLUMN_PRICE, 100);
        values.put(WatchesEntry.WATCHES_COLUMN_SUPPLIER_NAME, "nike");
        values.put(WatchesEntry.WATCHES_COLUMN_SUPPLIER_WEBSITE, "www.nike.com");

        resultUri = getContentResolver().insert(WatchesEntry.CONTENT_URI, values);
        resultRow = ContentUris.parseId(resultUri);
        if (resultRow != -1){
            Toast.makeText(getApplicationContext(), "watch added", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertData() throws IOException {

        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(selectedImageUri);
            byte[] bytes = getBytes(inputStream);

            String name = nameEditText.getText().toString();
            quantity = Integer.valueOf(quantityEditText.getText().toString().trim());
            price = Integer.valueOf(priceEditText.getText().toString().trim());
            String supplier = supplierEditText.getText().toString();
            String supplierWebsite = supplierWebsiteEditText.getText().toString();

            ContentValues values = new ContentValues();
            long resultRow = 0;
            Uri resultUri = null;
            int result = 0;

            values.put(WatchesEntry.WATCHES_COLUMN_NAME, name);

            values.put(WatchesEntry.WATCHES_COLUMN_PICTURE, bytes);

            values.put(WatchesEntry.WATCHES_COLUMN_TYPE, watchType);
            values.put(WatchesEntry.WATCHES_COLUMN_QUANTITY, quantity);
            values.put(WatchesEntry.WATCHES_COLUMN_PRICE, price);
            values.put(WatchesEntry.WATCHES_COLUMN_SUPPLIER_NAME, supplier);
            values.put(WatchesEntry.WATCHES_COLUMN_SUPPLIER_WEBSITE, supplierWebsite);

            resultUri = getContentResolver().insert(WatchesEntry.CONTENT_URI, values);
            resultRow = ContentUris.parseId(resultUri);

            if (resultRow != 0){
                Toast.makeText(getApplicationContext(), "watch added", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            inputStream.close();
        }

    }

    public void updateData(){

        byte[] bytes = retreivedImageBytes;

        String name = nameEditText.getText().toString();
        quantity = Integer.valueOf(quantityEditText.getText().toString().trim());
        price = Integer.valueOf(priceEditText.getText().toString().trim());
        String supplier = supplierEditText.getText().toString();
        String supplierWebsite = supplierWebsiteEditText.getText().toString();

        ContentValues values = new ContentValues();

        int result = 0;

        values.put(WatchesEntry.WATCHES_COLUMN_NAME, name);

        values.put(WatchesEntry.WATCHES_COLUMN_PICTURE, bytes);

        values.put(WatchesEntry.WATCHES_COLUMN_TYPE, watchType);
        values.put(WatchesEntry.WATCHES_COLUMN_QUANTITY, quantity);
        values.put(WatchesEntry.WATCHES_COLUMN_PRICE, price);
        values.put(WatchesEntry.WATCHES_COLUMN_SUPPLIER_NAME, supplier);
        values.put(WatchesEntry.WATCHES_COLUMN_SUPPLIER_WEBSITE, supplierWebsite);

        result = getContentResolver().update(receivedUri, values, null, null);

        if (result != 0){
            Toast.makeText(getApplicationContext(), "watch updated", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_data, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_leather))) {
                        watchType = WatchesEntry.WATCHES_TYPE_LEATHER; // Leather
                    } else if (selection.equals(getString(R.string.gender_stainless_steel))) {
                        watchType = WatchesEntry.WATCHES_TYPE_STAINLESS_STEEL; // Stainless Steel
                    } else if (selection.equals(getString(R.string.gender_rubber))){
                        watchType = WatchesEntry.WATCHES_TYPE_RUBBER; // Rubber
                    }else {
                        watchType = WatchesEntry.WATCHES_TYPE_UNKNOWN; // Unknown
                    }
                }*/
                int sel = parent.getSelectedItemPosition();
                switch (sel){

                    case 0:
                        watchType = WatchesEntry.WATCHES_TYPE_UNKNOWN;
                        break;
                    case 1:
                        watchType = WatchesEntry.WATCHES_TYPE_LEATHER;
                        break;
                    case 2:
                        watchType = WatchesEntry.WATCHES_TYPE_STAINLESS_STEEL;
                        break;
                    case 3:
                        watchType = WatchesEntry.WATCHES_TYPE_RUBBER;
                        break;
                    default:
                        watchType = WatchesEntry.WATCHES_TYPE_UNKNOWN;
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                watchType = WatchesEntry.WATCHES_TYPE_UNKNOWN; // Unknown
            }
        });
    }

    /*
    * this method os for emptying the edit texts and leave the activity after adding or updating a watch
    * */
    public void leaveActivity(){

        nameEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        supplierEditText.setText("");
        supplierWebsiteEditText.setText("");
        mGenderSpinner.setSelection(3);

        finish();
    }

    /*
    * this method is for deleting the current item of the listview that has been clicked
    * */
    public int deleteCurrentItem(Uri uri){

        int resultRow = getContentResolver().delete(uri, null, null);

        if (resultRow != 0){
            Toast.makeText(getApplicationContext(), "watch deleted", Toast.LENGTH_SHORT).show();
            return resultRow;
        }else {
            Toast.makeText(getApplicationContext(), "error deleting watch", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    //this method checks weather the entered value is only numbers
    public boolean isValidNumber(String value){

        boolean check = false;
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()){
            check = true;
        }else {
            check = false;
        }
        return check;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //set the projection
        String[] projection = {WatchesEntry._ID, WatchesEntry.WATCHES_COLUMN_NAME
                , WatchesEntry.WATCHES_COLUMN_PICTURE
                , WatchesEntry.WATCHES_COLUMN_TYPE
                , WatchesEntry.WATCHES_COLUMN_QUANTITY
                , WatchesEntry.WATCHES_COLUMN_PRICE
                , WatchesEntry.WATCHES_COLUMN_SUPPLIER_NAME
                , WatchesEntry.WATCHES_COLUMN_SUPPLIER_WEBSITE};

        return new CursorLoader(getApplicationContext(), receivedUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()){

            int nameIndex = cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_NAME);
            int pictureIndex = cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_PICTURE);
            int quantityIndex = cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_QUANTITY);
            int priceIndex = cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_PRICE);
            int supplierIndex = cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_SUPPLIER_NAME);
            int supplierWebsiteIndex = cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_SUPPLIER_WEBSITE);

            String name = cursor.getString(nameIndex);
            retreivedImageBytes = cursor.getBlob(pictureIndex);
            Bitmap bitmap = BitmapFactory.decodeByteArray(retreivedImageBytes, 0, retreivedImageBytes.length);
            //use the global variable of the quantity so that when we access it in the update mode
            // we get a reference to the retrieved value and we can increase or decrease it by the plus and minus buttons
            //instead of creating a new value
            quantity = cursor.getInt(quantityIndex);
            int price = cursor.getInt(priceIndex);
            String supplier = cursor.getString(supplierIndex);
            String supplierSite = cursor.getString(supplierWebsiteIndex);

            nameEditText.setText(name);
            imageView.setImageBitmap(bitmap);
            quantityEditText.setText(String.valueOf(quantity));
            priceEditText.setText(String.valueOf(price));
            supplierEditText.setText(supplier);
            supplierWebsiteEditText.setText(supplierSite);

            watchType = cursor.getInt(cursor.getColumnIndex(WatchesEntry.WATCHES_COLUMN_TYPE));
            switch (watchType){
                case 0:
                    mGenderSpinner.setSelection(WatchesEntry.WATCHES_TYPE_UNKNOWN);
                    break;
                case 1:
                    mGenderSpinner.setSelection(WatchesEntry.WATCHES_TYPE_LEATHER);
                    break;
                case 2:
                    mGenderSpinner.setSelection(WatchesEntry.WATCHES_TYPE_STAINLESS_STEEL);
                    break;
                default:
                    mGenderSpinner.setSelection(WatchesEntry.WATCHES_TYPE_RUBBER);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //this method is for showing a dialog to make the user confirm leaving the editing
    // or staying in the editing page
    //we only handle the "keep editing" button in this method the
    //"discard" button needs to be handled explicitly in the "up" and "back" methods
    public void discardChangesDialog(DialogInterface.OnClickListener discardButtonInterfaceListener){

        //the builder class helps creating the parts of the dialog such as title, message and buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("discard editing");
        builder.setMessage("want to discard editing ??");
        builder.setPositiveButton("stay here", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setNegativeButton("leave", discardButtonInterfaceListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {


        /// Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that
        // changes should be discarded and then we pass it to the method that shows the dialog
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        discardChangesDialog(discardButtonClickListener);
    }
}
