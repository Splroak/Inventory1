package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import static android.R.attr.name;

/**
 * Created by Splroak on 9/13/2017.
 */

public class InventoryProvider extends ContentProvider {
    //Log tag for this activity
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    //Database helper object
    private InventoryDbHelper mDbHelper;

    //Uri matcher code for the content URI for the inventory table
    public static final int INVENTORY = 100;

    //Uri matcher code for the content URI for an item in inventory table
    public static final int INVENTORY_ITEM = 101;
    /**
     * URI matcher object to match a context URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //This UriMatcher maps to the code 100, which will provide access to multiple rows in inventory table
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);

        //This UriMatcher maps to the code 101, which will provide access to a single row in inventory table
        // "/#" wildcard can be substituted by an ID.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ITEM);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ITEM:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ITEM:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    
    @Override
    public Uri insert( Uri uri,  ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri insertUri = null;
        switch (match){
            case INVENTORY:
                insertUri = insertInventory(uri, values);
                if(insertUri != null){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return insertUri;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
private Uri insertInventory(Uri uri, ContentValues values){
    // Get writable database
    SQLiteDatabase database = mDbHelper.getWritableDatabase();
    // Insert the new item with the given values
    long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
    if (id == -1) {
        Log.e(LOG_TAG, "Failed to insert row for " + uri);
        return null;}
    // Check that the name is not null
    String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
    if (name == null) {
        throw new IllegalArgumentException("Pet requires a name");}
    // Once we know the ID of the new row in the table,
    // return the new URI with the ID appended to the end of it
    return ContentUris.withAppendedId(uri, id);
}
    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int deleteCount = 0;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // Delete all rows that match the selection and selection args
                deleteCount = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if(deleteCount > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return deleteCount;
            case INVENTORY_ITEM:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                deleteCount = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if(deleteCount > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return deleteCount;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        int updateCount = 0;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                updateCount = updateInventory(uri, values, selection, selectionArgs);
                if(updateCount > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return updateCount;
            case INVENTORY_ITEM:
                // For the INVENTORY_ITEM code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                updateCount = updateInventory(uri, values, selection, selectionArgs);
                if(updateCount > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return updateCount;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateInventory (Uri uri, ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.update(InventoryEntry.TABLE_NAME, values,selection,selectionArgs);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return 0;}
        // Check that the weight is not null
        if(values.containsKey(InventoryEntry.COLUMN_ITEM_PRICE)){
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_ITEM_PRICE);
            if(price != null && price <0);{
                throw new IllegalArgumentException("Item needs a price");
            }
        }
        if(values.containsKey(InventoryEntry.COLUMN_ITEM_QUANTITY)){
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Item needs quantity");}
        }
        if(values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)){
            String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if(name == null){
                throw new IllegalArgumentException("Item needs a name");
            }
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        if(values.size() == 0){
            return 0;
        }
        return database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs); 
    }
}
