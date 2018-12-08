package com.alisakralarabygmail.watchesinventory.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class WatchesContract {

    //constant for the content authority
    public static final String CONTENT_AUTHORITY = "com.alisakralarabygmail.watchesinventory";

    //constant for the base content path
    //"content://com.alisakralarabygmail.watchesinventory"
    public static final Uri BASE_CONTENT = Uri.parse("content://" + CONTENT_AUTHORITY);

    //constant for the watches table part of the path
    public static final String WATCHESINVENTORY_PATH = "watches";

    //inner class for the watches table
    public static class WatchesEntry implements BaseColumns {

        //constants for the watches table columns` names
        public static final String WATCHES_TABLE_NAME = "watches";
        public static final String _ID = BaseColumns._ID;
        public static final String WATCHES_COLUMN_NAME = "name";
        public static final String WATCHES_COLUMN_QUANTITY = "quantity";
        public static final String WATCHES_COLUMN_PRICE = "price";
        public static final String WATCHES_COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String WATCHES_COLUMN_SUPPLIER_WEBSITE = "supplier_website";
        public static final String WATCHES_COLUMN_PICTURE = "picture";
        public static final String WATCHES_COLUMN_TYPE = "type";

        //constants for the "type" column 3 values
        //0 for leather
        //1 for stainless steel
        //2 for rubber
        public static final int WATCHES_TYPE_LEATHER = 1;
        public static final int WATCHES_TYPE_STAINLESS_STEEL = 2;
        public static final int WATCHES_TYPE_RUBBER = 3;
        public static final int WATCHES_TYPE_UNKNOWN = 0;

        //constant for the full path of the content uri of the watches table
        //"content://com.alisakralarabygmail.watchesinventory"
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT, WATCHESINVENTORY_PATH);
    }
}
