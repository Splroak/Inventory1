package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.example.android.inventory.data.InventoryContract;

/**
 * Created by Splroak on 9/13/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter{
    /** Constructs a new Adapter
     * @param context The context
     * @param c The cursor from which to get the data.*/

    public InventoryCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    /**
     *
     * @param context app context
     * @param cursor The cursor from which to get the data. The cursor is already moved to the correct position.
     * @param parent The parent to which the new view is attached to.
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
// Find fields to populate in inflated template
        TextView textViewName = (TextView) view.findViewById(R.id.name);
        TextView textViewQuantity = (TextView) view.findViewById(R.id.quantity);
        TextView textViewPrice = (TextView) view.findViewById(R.id.price);

        //Find the column of pets that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);

        // Extract properties from cursor
        String name = cursor.getString(nameColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        // Populate fields with extracted properties
        textViewName.setText(name);
        textViewQuantity.setText(quantity);
        textViewPrice.setText(price);
    }
}
