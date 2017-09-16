package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import static android.R.attr.data;

/**
 * Created by Splroak on 9/14/2017.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //TextView to inflate the name of the item
    private TextView mNameTextView;

    //TextView to inflate the quantity of the item
    private TextView mQuantityTextView;

    //TextView to inflate the price of the item
    private TextView mPriceTextView;

    //Content URI of the clicked item
    private Uri mCurrentItemUri;

    //Global variable to work with increment and decrement methods
    private int quantity;
    //Increment button
    private Button mIncrementButton;

    //Decrement button
    private Button mDecrementButton;

    //Order button
    private Button mOrderButton;

    //Delete button
    private Button mDeleteButton;
    //Identifier for the item loader
    private static final int INVENTORY_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        //Find view to inflate the categories
        mNameTextView = (TextView) findViewById(R.id.detail_view_name);
        mQuantityTextView = (TextView) findViewById(R.id.detail_view_quantity);
        mPriceTextView = (TextView) findViewById(R.id.detail_view_price);

        //Find view to inflate the buttons
        mIncrementButton = (Button) findViewById(R.id.detail_view_increment);
        mDecrementButton = (Button) findViewById(R.id.detail_view_decrement);
        mOrderButton = (Button) findViewById(R.id.detail_view_order);
        mDeleteButton = (Button) findViewById(R.id.detail_view_delete);
        //Assign the increment task for the button
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment();
            }
        });
        //Assign the decrement task for the button
        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry.COLUMN_ITEM_QUANTITY
        };
        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of inventory attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            // Update the views on the screen with the values from the database
            mNameTextView.setText(name);
            mQuantityTextView.setText(quantity + "");
            mPriceTextView.setText(price + "");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameTextView.setText("");
        mQuantityTextView.setText("");
        mPriceTextView.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case android.R.id.home:
                updateQuantity();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void increment() {
        quantity += 1;
        TextView quantityTextView = (TextView) findViewById(R.id.detail_view_quantity);
        quantityTextView.setText(quantity + "");
    }

    public void decrement() {
        if (quantity == 1) {
            Toast.makeText(getApplicationContext(), getString(R.string.decrement_toast), Toast.LENGTH_SHORT).show();
            return;
        } else {
            quantity = quantity - 1;
        }
        TextView quantityTextView = (TextView) findViewById(R.id.detail_view_quantity);
        quantityTextView.setText(quantity + "");
    }

    private void updateQuantity() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY,quantity);
        int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, "fucked up",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, "solid",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
