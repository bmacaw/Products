package com.example.android.products;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.products.data.ProductContract.ProductEntry;


public class ProductCursorAdaptor extends CursorAdapter {
    private Context mContexts;


    public ProductCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0);
        mContexts = context;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name_list_view_text_view);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_list_view_text_view);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_list_view_text_view);
        Button sellOneButton = (Button) view.findViewById(R.id.list_view_sell_button);

        final String productId = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry._ID));
        final String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final double priceValue = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));

        final Cursor cursorValue = cursor;
        String quantityString = "In Stock: " + quantity;
        String priceString = "Price $" + priceValue;
        final int position = cursor.getPosition();
        final Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, Long.parseLong(productId));

        nameTextView.setText(productName);
        quantityTextView.setText(quantityString);
        priceTextView.setText(priceString);

        sellOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cursor.moveToPosition(position);

                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();

                if (quantity > 0) {

                    int quantity = cursorValue.getInt(cursorValue.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
                    int sold = cursorValue.getInt(cursorValue.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD));

                    int quantityValue = quantity;
                    int soldValue = sold;

                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --quantityValue);
                    values.put(ProductEntry.COLUMN_PRODUCT_SOLD, ++soldValue);

                    resolver.update(currentProductUri, values, null, null);
                    mContexts.getContentResolver().notifyChange(currentProductUri, null);

                    Toast.makeText(context, "Sold one " + productName, Toast.LENGTH_SHORT).show();

                } else if (quantity == 0) {

                    Toast.makeText(context, R.string.quantity_is_zero_result_toast, Toast.LENGTH_SHORT).show();
                }
            }

            /*{


                int quantity;
                if (quantityTextView.getText().toString().isEmpty()) {
                    quantity = 0;
                } else {
                    quantity = Integer.parseInt(quantityTextView.getText().toString());
                }

                if (quantity > 0) {
                    quantity = quantity - 1;
                    quantityTextView.setText(String.valueOf(quantity));

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceValue);
                    //values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, image);
                    //values.put(ProductEntry.COLUMN_PRODUCT_SOLD, sold);
                    //values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);

                    int rowsAffected = context.getContentResolver().update(currentProductUri, values, null, null);
                    if(rowsAffected == 0) {
                        Toast.makeText(context, R.string.editor_update_product_failed, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Sold one " + productName, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(context, R.string.quantity_is_zero_result_toast, Toast.LENGTH_SHORT).show();
                }
            }*/
        });

        /*{
                int quantity;
                if (quantityTextView.getText().toString().isEmpty()) {
                    quantity = 0;
                } else {
                    quantity = Integer.parseInt(quantityTextView.getText().toString());
                }
                if (quantity > 0) {
                    quantity = quantity - 1;
                    quantityTextView.setText(String.valueOf(quantity));
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceValue);
                    //values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, image);
                    //values.put(ProductEntry.COLUMN_PRODUCT_SOLD, sold);
                    //values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
                    int rowsAffected = context.getContentResolver().update(currentProductUri, values, null, null);
                    if(rowsAffected == 0) {
                        Toast.makeText(context, R.string.editor_update_product_failed, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Sold one " + productName, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(context, R.string.quantity_is_zero_result_toast, Toast.LENGTH_SHORT).show();
                }
            }*/
    }
}

