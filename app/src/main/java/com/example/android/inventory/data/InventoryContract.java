package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Splroak on 9/13/2017.
 */

public class InventoryContract {
    /**
     * Name of the content authority
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    /**
     * Create a base content URI
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Path of the table name
     */
    public static final String PATH_INVENTORY = "inventory";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }
    /** Inner class that defines constant values for the inventory table*/
    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
        /** Name of the table */
        public final static String TABLE_NAME = "inventory";

        //ID number for the item
        // TYPE: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Name of the item
        //TYPE: TEXT
        public final static String COLUMN_ITEM_NAME = "name";

        //Quantity of the item
        //TYPE: INTEGER
        public final static String COLUMN_ITEM_QUANTITY = "quantity";

        //Price of the item
        //TYPE: INTEGER
        public final static String COLUMN_ITEM_PRICE = "price";
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
    }
}
